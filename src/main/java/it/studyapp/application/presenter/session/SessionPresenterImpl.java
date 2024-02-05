package it.studyapp.application.presenter.session;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;

import it.studyapp.application.Application;
import it.studyapp.application.entity.NotificationEntity;
import it.studyapp.application.entity.Session;
import it.studyapp.application.entity.SessionRequest;
import it.studyapp.application.entity.Student;
import it.studyapp.application.event.NotificationCreatedEvent;
import it.studyapp.application.event.SessionRemovedEvent;
import it.studyapp.application.event.SessionRequestAcceptedEvent;
import it.studyapp.application.event.SessionRequestCreatedEvent;
import it.studyapp.application.event.SessionUpdatedEvent;
import it.studyapp.application.security.SecurityService;
import it.studyapp.application.service.DataService;
import it.studyapp.application.ui.dialog.SessionDialog;
import it.studyapp.application.view.session.SessionView;

@Component
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SessionPresenterImpl implements SessionPresenter {
	
	private SessionView view;
	private final Logger logger = LoggerFactory.getLogger(SessionPresenterImpl.class);
	
	@Autowired
	private DataService dataService;
	
	@Autowired
	private SecurityService securityService;
	
	private Session selectedSession = null;
	
	public SessionPresenterImpl() {
		UI currentUI = UI.getCurrent();
		
		if(currentUI != null) {
			ComponentUtil.addListener(currentUI, SessionRequestAcceptedEvent.class, e -> updateSessionGrid());
			ComponentUtil.addListener(currentUI, SessionUpdatedEvent.class, e -> updateSessionGrid());
			ComponentUtil.addListener(currentUI, SessionRemovedEvent.class, e -> updateSessionGrid());
		}
	}
		
	@Override
	public void setView(SessionView view) {
		this.view = view;
	}

	@Override
	public void updateSessionGrid() {	
		Student thisStudent = dataService.searchStudent(securityService.getAuthenticatedUser().getUsername()).get(0);
	
		List<Session> lss = thisStudent.getSessions();
		Collections.sort(lss, Comparator.comparing(Session::getDate));
		
		view.setSessionGridItems(lss);
		view.setSessionGridCount(lss.size());
		
		/* A session is selected */
		if(selectedSession != null) {
			selectedSession = dataService.findSessionById(selectedSession.getId());
			
			/* The session exists for the student */
			if(selectedSession != null && selectedSession.getParticipants().contains(thisStudent)) {
				view.setParticipantsGridItems(selectedSession.getParticipants());
				view.setParticipantsGridCount(selectedSession.getParticipants().size());
			} else {
				view.hideParticipants();
			}
		}
	}

	@Override
	public void createSession() {
		selectedSession = null;
		view.hideParticipants();
		
		logger.info(securityService.getAuthenticatedUser().getUsername() + " is creating a session.");
		
		SessionDialog sessionDiag = new SessionDialog(dataService, securityService, null, null);
		sessionDiag.setOnSaveBiConsumer(this::onSessionCreated);
		sessionDiag.open();	
	}

	@Override
	public void leaveSession() {
		Student thisStudent = dataService.searchStudent(securityService.getAuthenticatedUser().getUsername()).get(0);
		
		logger.info(thisStudent.getUsername() + " left session " + selectedSession.getId());
		
		if(thisStudent.equals(selectedSession.getOwner())) {
			if(selectedSession.getParticipants().size() > 1) {
				selectedSession.setOwner(selectedSession.getParticipants().get(1));
				logger.info(selectedSession.getOwner() + " is now the owner of session " + selectedSession.getId());
			}
			else {
				logger.info("No participants left in session " + selectedSession.getId());
				onSessionRemoved(selectedSession);
				view.hideParticipants();
				return;
			}
		}
		
		selectedSession.removeParticipant(thisStudent);
		selectedSession = dataService.saveSession(selectedSession);
		
		selectedSession.getParticipants().forEach(s -> {
			UI ui = Application.getUserUI(s.getUsername());
			if(ui != null)
				ui.access(() -> ComponentUtil.fireEvent(ui, new SessionUpdatedEvent(UI.getCurrent(), false)));
		});
		
		UI ui = Application.getUserUI("admin");
		if(ui != null)
			ui.access(() -> ComponentUtil.fireEvent(ui, new SessionUpdatedEvent(UI.getCurrent(), false)));
		
		selectedSession = null;
		view.hideParticipants();
		updateSessionGrid();
	}

	@Override
	public void onSessionClick(Session session) {
		selectedSession = session;
		
		List<Student> ls = session.getParticipants();
		view.setParticipantsGridItems(ls);
		view.setParticipantsGridCount(ls.size());
		view.showParticipants();
	}

	@Override
	public void onSessionDoubleClick(Session session) {
		if(!session.getOwner().getUsername().equals(securityService.getAuthenticatedUser().getUsername()))
			return;
		
		logger.info(securityService.getAuthenticatedUser().getUsername() + " clicked session " + session.getId());
		
		SessionDialog sessionDiag = new SessionDialog(dataService, securityService, session, null);
		sessionDiag.setOnSaveBiConsumer(this::onSessionUpdated);
		sessionDiag.setOnRemoveConsumer(this::onSessionRemoved);
		sessionDiag.open();		
	}

	@Override
	public void onParticipantDoubleClick(Student clickedStudent) {
		if(clickedStudent.getUsername().equals(securityService.getAuthenticatedUser().getUsername())) {
			UI.getCurrent().navigate("profile/me");
		} else {
			UI.getCurrent().navigate("profile/" + clickedStudent.getUsername());
		}		
	}
	
	private void onSessionCreated(Session session, Set<Student> selectedStudents) {
		logger.info(securityService.getAuthenticatedUser().getUsername() + " created session " + session.getId());

		session.addParticipant(session.getOwner());
		dataService.saveSession(session);
		
		selectedStudents.forEach(s -> {
			SessionRequest sr = new SessionRequest(session.getOwner().getUsername() + 
					" invited you to the study session " + session.getSubject(), s, session.getId());
			final SessionRequest persistentSR = dataService.saveSessionRequest(sr);

			UI ui = Application.getUserUI(s.getUsername());
			if(ui != null)
				ui.access(() -> ComponentUtil.fireEvent(ui, new SessionRequestCreatedEvent(UI.getCurrent(), false, persistentSR)));

		});
		
		StringBuilder createLog = new StringBuilder();
		createLog.append("Session " + session.getId() + " created by " + session.getOwner() + ". Invites sent to: ");
		createLog.append(String.join(", ", selectedStudents.stream().map(Student::getUsername).toList()));
		
		logger.info(createLog.toString());
		
		UI ui = Application.getUserUI("admin");
		if(ui != null)
			ui.access(() -> ComponentUtil.fireEvent(ui, new SessionUpdatedEvent(UI.getCurrent(), false)));

		updateSessionGrid();
	}

	private void onSessionRemoved(Session session) {
		logger.info(securityService.getAuthenticatedUser().getUsername() + " removed session " + session.getId());

		Set<Student> sessionParticipants = new HashSet<>(session.getParticipants());

		dataService.deleteSession(session);
		
		List<SessionRequest> requests = dataService.searchSessionRequests(session.getId());
		if(requests != null && !requests.isEmpty()) {
			requests.forEach(request -> {
				if(!request.isAccepted())
					dataService.deleteSessionRequest(request);
			});
		}

		sessionParticipants.forEach(s -> {
			UI ui = Application.getUserUI(s.getUsername());
			if(ui != null)
				ui.access(() -> ComponentUtil.fireEvent(ui, new SessionRemovedEvent(UI.getCurrent(), false)));
		});
		
		UI ui = Application.getUserUI("admin");
		if(ui != null)
			ui.access(() -> ComponentUtil.fireEvent(ui, new SessionRemovedEvent(UI.getCurrent(), false)));
	}

	private void onSessionUpdated(Session session, Set<Student> unmodifiableSelectedStudents) {
		logger.info(securityService.getAuthenticatedUser().getUsername() + " updated session " + session.getId());

		Set<Student> oldParticipants = new HashSet<Student>(session.getParticipants());
		Set<Student> selectedStudents = new HashSet<>(unmodifiableSelectedStudents);
		Set<Student> removedStudents = new HashSet<>();
		Set<Student> invitedStudents = new HashSet<>();

		session.getParticipants().forEach(s -> {
			if(s.getUsername().equals(securityService.getAuthenticatedUser().getUsername()))
				return;

			if(!selectedStudents.contains(s))
				removedStudents.add(s);	
		});

		session.removeAllParticipants();
		session.addParticipant(session.getOwner());

		selectedStudents.forEach(s -> {
			if(oldParticipants.contains(s)) {
				
				NotificationEntity n = new NotificationEntity(session.getOwner().getUsername() + 
						" made changes to one of your sessions", s);
				final NotificationEntity persistentN = dataService.saveNotification(n);
				
				session.addParticipant(s);

				UI ui = Application.getUserUI(s.getUsername());
				if(ui != null)
					ui.access(() -> ComponentUtil.fireEvent(ui, new NotificationCreatedEvent(UI.getCurrent(), false, persistentN)));	

			} else {
				invitedStudents.add(s);
				
				SessionRequest sr = new SessionRequest(session.getOwner().getUsername() + 
						" invited you to the study session " + session.getSubject(), s, session.getId());
				final SessionRequest persistentSR = dataService.saveSessionRequest(sr);

				UI ui = Application.getUserUI(s.getUsername());
				if(ui != null)
					ui.access(() -> ComponentUtil.fireEvent(ui, new SessionRequestCreatedEvent(UI.getCurrent(), false, persistentSR)));
			}
		});

		Session persistentSession = dataService.saveSession(session);	

		StringBuilder updateLog = new StringBuilder();
		updateLog.append("Session " + persistentSession.getId() + " updated by " + persistentSession.getOwner() + ". Participants: ");
		updateLog.append(String.join(", ", persistentSession.getParticipants().stream().map(Student::getUsername).toList()));
		updateLog.append(". Removed students: ");
		updateLog.append(String.join(", ", removedStudents.stream().map(Student::getUsername).toList()));
		updateLog.append(". Invites sent to: ");
		updateLog.append(String.join(", ", invitedStudents.stream().map(Student::getUsername).toList()));
		
		logger.info(updateLog.toString());

		persistentSession.getParticipants().forEach(s -> {
			UI ui = Application.getUserUI(s.getUsername());
			if(ui != null)
				ui.access(() -> ComponentUtil.fireEvent(ui, new SessionUpdatedEvent(UI.getCurrent(), false)));
		});

		removedStudents.forEach(s -> {
			UI ui = Application.getUserUI(s.getUsername());
			if(ui != null)
				ui.access(() -> ComponentUtil.fireEvent(ui, new SessionUpdatedEvent(UI.getCurrent(), false)));
		});
		
		UI ui = Application.getUserUI("admin");
		if(ui != null)
			ui.access(() -> ComponentUtil.fireEvent(ui, new SessionUpdatedEvent(UI.getCurrent(), false)));

	}

}
