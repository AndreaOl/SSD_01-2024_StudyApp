package it.studyapp.application.presenter.admin;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;

import it.studyapp.application.Application;
import it.studyapp.application.entity.Session;
import it.studyapp.application.entity.SessionRequest;
import it.studyapp.application.entity.Student;
import it.studyapp.application.entity.StudentGroup;
import it.studyapp.application.entity.StudentGroupRequest;
import it.studyapp.application.event.ProfileUpdatedEvent;
import it.studyapp.application.event.SessionRemovedEvent;
import it.studyapp.application.event.SessionRequestAcceptedEvent;
import it.studyapp.application.event.SessionUpdatedEvent;
import it.studyapp.application.event.StudentCreatedEvent;
import it.studyapp.application.event.StudentGroupRemovedEvent;
import it.studyapp.application.event.StudentGroupRequestAcceptedEvent;
import it.studyapp.application.event.StudentGroupUpdatedEvent;
import it.studyapp.application.security.SecurityService;
import it.studyapp.application.service.DataService;
import it.studyapp.application.ui.dialog.SessionDialog;
import it.studyapp.application.ui.dialog.StudentGroupDialog;
import it.studyapp.application.view.admin.AdminView;

@Component
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AdminPresenterImpl implements AdminPresenter {
	
	private AdminView view;
	
	@Autowired
	private DataService dataService;
	
	@Autowired
	private SecurityService securityService;
	
	public AdminPresenterImpl() {
		UI currentUI = UI.getCurrent();
		
		if(currentUI != null) {
			ComponentUtil.addListener(currentUI, SessionRequestAcceptedEvent.class, e -> updateSessionGrid());
			ComponentUtil.addListener(currentUI, SessionUpdatedEvent.class, e -> updateSessionGrid());
			ComponentUtil.addListener(currentUI, SessionRemovedEvent.class, e -> updateSessionGrid());
			
			ComponentUtil.addListener(UI.getCurrent(), StudentGroupRequestAcceptedEvent.class, e -> updateGroupGrid());
			ComponentUtil.addListener(UI.getCurrent(), StudentGroupUpdatedEvent.class, e -> updateGroupGrid());
			ComponentUtil.addListener(UI.getCurrent(), StudentGroupRemovedEvent.class, e -> updateGroupGrid());
			
			ComponentUtil.addListener(currentUI, StudentCreatedEvent.class, e -> updateStudentGrid());
			ComponentUtil.addListener(currentUI, ProfileUpdatedEvent.class, e -> updateStudentGrid());
		}
	}

	@Override
	public void setView(AdminView view) {
		this.view = view;
	}
	
	
	
	/*-------------------------------------------------------*
	 *                    Session Methods                    *
	 * ------------------------------------------------------*/

	@Override
	public void updateSessionGrid() {		
		List<Session> lss = dataService.findAllSessions();
		Collections.sort(lss, Comparator.comparing(Session::getDate));
		
		view.setSessionGridItems(lss);
		view.setSessionGridCount(lss.size());
	}

	@Override
	public void createSession() {		
		SessionDialog sessionDiag = new SessionDialog(dataService, securityService, null, null);
		sessionDiag.setOnSaveBiConsumer(this::onSessionCreated);
		sessionDiag.open();	
	}

	@Override
	public void onSessionDoubleClick(Session session) {
		SessionDialog sessionDiag = new SessionDialog(dataService, securityService, session, null);
		sessionDiag.setOnSaveBiConsumer(this::onSessionUpdated);
		sessionDiag.setOnRemoveConsumer(this::onSessionRemoved);
		sessionDiag.open();		
	}

	private void onSessionCreated(Session session, Set<Student> selectedStudents) {
		session.addParticipants(selectedStudents);
		session.setOwner(session.getParticipants().get(0));
		dataService.saveSession(session);
		
		selectedStudents.forEach(s -> {
			UI ui = Application.getUserUI(s.getUsername());
			if(ui != null)
				ui.access(() -> ComponentUtil.fireEvent(ui, new SessionUpdatedEvent(UI.getCurrent(), false)));
		});
	
		updateSessionGrid();
	}

	private void onSessionRemoved(Session session) {
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
		
		updateSessionGrid();
	}

	private void onSessionUpdated(Session session, Set<Student> selectedStudents) {
		
		if(selectedStudents.isEmpty()) {
			onSessionRemoved(session);
			return;
		}
		
		Set<Student> removedStudents = new HashSet<>();
	
		session.getParticipants().forEach(s -> {	
			if(!selectedStudents.contains(s))
				removedStudents.add(s);	
		});
	
		session.removeAllParticipants();
		session.addParticipants(selectedStudents);
		
		if(!selectedStudents.contains(session.getOwner()))
			session.setOwner(session.getParticipants().get(0));
	
		Session persistentSession = dataService.saveSession(session);	
	
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
	
		updateSessionGrid();
	}
	
	
	
	/*-------------------------------------------------------*
	 *                    Group Methods                      *
	 * ------------------------------------------------------*/

	@Override
	public void updateGroupGrid() {		
		List<StudentGroup> lg = dataService.findAllStudentGroups();
		view.setGroupGridItems(lg);
		view.setGroupGridCount(lg.size());
	}

	@Override
	public void createGroup() {		
		StudentGroupDialog studentGroupDialog = new StudentGroupDialog(dataService, securityService, null);
		studentGroupDialog.setOnSaveBiConsumer(this::onStudentGroupCreated);
		studentGroupDialog.open();		
	}

	@Override
	public void onGroupDoubleClick(StudentGroup studentGroup) {
		StudentGroupDialog studentGroupDialog = new StudentGroupDialog(dataService, securityService, studentGroup);
		studentGroupDialog.setOnSaveBiConsumer(this::onStudentGroupUpdated);
		studentGroupDialog.setOnRemoveConsumer(this::onStudentGroupRemoved);
		studentGroupDialog.open();		
	}
	
	private void onStudentGroupCreated(StudentGroup studentGroup, Set<Student> selectedStudents) {
		studentGroup.addMembers(selectedStudents);
		studentGroup.setOwner(studentGroup.getMembers().get(0));
		dataService.saveStudentGroup(studentGroup);

		selectedStudents.forEach(s -> {			
			UI ui = Application.getUserUI(s.getUsername());
			if(ui != null)
				ui.access(() -> ComponentUtil.fireEvent(ui, new StudentGroupUpdatedEvent(UI.getCurrent(), false)));
		});

		updateGroupGrid();
	}

	private void onStudentGroupRemoved(StudentGroup studentGroup) {
		Set<Student> groupMembers = new HashSet<>(studentGroup.getMembers());

		dataService.deleteStudentGroup(studentGroup);
		
		List<StudentGroupRequest> requests = dataService.searchStudentGroupRequests(studentGroup.getId());
		if(requests != null && !requests.isEmpty()) {
			requests.forEach(request -> {
				if(!request.isAccepted())
					dataService.deleteStudentGroupRequest(request);
			});
		}
		
		groupMembers.forEach(s -> {
			UI ui = Application.getUserUI(s.getUsername());
			if(ui != null)
				ui.access(() -> ComponentUtil.fireEvent(ui, new StudentGroupRemovedEvent(UI.getCurrent(), false)));
		});
		
		updateGroupGrid();
	}

	private void onStudentGroupUpdated(StudentGroup studentGroup, Set<Student> selectedStudents) {
		
		if(selectedStudents.isEmpty()) {
			onStudentGroupRemoved(studentGroup);
			return;
		}
		
		Set<Student> removedStudents = new HashSet<>();
		
		studentGroup.getMembers().forEach(s -> {			
			if(!selectedStudents.contains(s))
				removedStudents.add(s);
		});
		
		studentGroup.removeAllMembers();
		studentGroup.addMembers(selectedStudents);
		
		if(!selectedStudents.contains(studentGroup.getOwner()))
			studentGroup.setOwner(studentGroup.getMembers().get(0));

		StudentGroup persistentStudentGroup = dataService.saveStudentGroup(studentGroup);
		
		persistentStudentGroup.getMembers().forEach(s -> {			
			UI ui = Application.getUserUI(s.getUsername());
			if(ui != null)
				ui.access(() -> ComponentUtil.fireEvent(ui, new StudentGroupUpdatedEvent(UI.getCurrent(), false)));
		});
		
		removedStudents.forEach(s -> {
			UI ui = Application.getUserUI(s.getUsername());
			if(ui != null)
				ui.access(() -> ComponentUtil.fireEvent(ui, new StudentGroupUpdatedEvent(UI.getCurrent(), false)));
		});
		
		updateGroupGrid();
	}

	
	
	/*-------------------------------------------------------*
	 *                    Student Methods                    *
	 * ------------------------------------------------------*/

	@Override
	public void updateStudentGrid() {
		List<Student> ls = dataService.findAllStudents();
		view.setStudentGridItems(ls);
		view.setStudentGridCount(ls.size());
	}

	@Override
	public void onStudentDoubleClick(Student clickedStudent) {
		if(clickedStudent.getUsername().equals(securityService.getAuthenticatedUser().getUsername())) {
			UI.getCurrent().navigate("profile/me");
		} else {
			UI.getCurrent().navigate("profile/" + clickedStudent.getUsername());
		}		
	}

}
