package it.studyapp.application.presenter.group;

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
import it.studyapp.application.entity.StudentGroup;
import it.studyapp.application.entity.StudentGroupRequest;
import it.studyapp.application.event.NotificationCreatedEvent;
import it.studyapp.application.event.SessionRequestCreatedEvent;
import it.studyapp.application.event.SessionUpdatedEvent;
import it.studyapp.application.event.StudentGroupRemovedEvent;
import it.studyapp.application.event.StudentGroupRequestAcceptedEvent;
import it.studyapp.application.event.StudentGroupRequestCreatedEvent;
import it.studyapp.application.event.StudentGroupUpdatedEvent;
import it.studyapp.application.security.SecurityService;
import it.studyapp.application.service.DataService;
import it.studyapp.application.ui.dialog.SessionDialog;
import it.studyapp.application.ui.dialog.StudentGroupDialog;
import it.studyapp.application.view.group.GroupView;

@Component
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class GroupPresenterImpl implements GroupPresenter {

	private GroupView view;
	private final Logger logger = LoggerFactory.getLogger(GroupPresenterImpl.class);
	
	@Autowired
	private DataService dataService;
	
	@Autowired
	private SecurityService securityService;
	
	private StudentGroup selectedGroup = null;
	
	public GroupPresenterImpl() {
		UI currentUI = UI.getCurrent();
		
		if(currentUI != null) {
			ComponentUtil.addListener(UI.getCurrent(), StudentGroupRequestAcceptedEvent.class, e -> updateGroupGrid());
			ComponentUtil.addListener(UI.getCurrent(), StudentGroupUpdatedEvent.class, e -> updateGroupGrid());
			ComponentUtil.addListener(UI.getCurrent(), StudentGroupRemovedEvent.class, e -> updateGroupGrid());
		}
	}
	
	@Override
	public void setView(GroupView view) {
		this.view = view;		
	}
	
	@Override
	public void updateGroupGrid() {	
		Student thisStudent = dataService.searchStudent(securityService.getAuthenticatedUser().getUsername()).get(0);
	
		List<StudentGroup> lg = thisStudent.getStudentGroups();
		
		
		view.setGroupGridItems(lg);
		view.setGroupGridCount(lg.size());
		
		/* A group is selected */
		if(selectedGroup != null) {
			selectedGroup = dataService.findStudentGroupById(selectedGroup.getId());
			
			/* The group exists for the student */
			if(selectedGroup != null && selectedGroup.getMembers().contains(thisStudent)) {
				view.setMembersGridItems(selectedGroup.getMembers());
				view.setMembersGridCount(selectedGroup.getMembers().size());
			} else {
				view.hideMembers();
			}
		}
	}

	@Override
	public void createGroup() {
		selectedGroup = null;
		view.hideMembers();
		
		logger.info(securityService.getAuthenticatedUser().getUsername() + " is creating a group.");
		
		StudentGroupDialog studentGroupDialog = new StudentGroupDialog(dataService, securityService, null);
		studentGroupDialog.setOnSaveBiConsumer(this::onStudentGroupCreated);
		studentGroupDialog.open();		
	}

	@Override
	public void leaveGroup() {
		Student thisStudent = dataService.searchStudent(securityService.getAuthenticatedUser().getUsername()).get(0);
		
		logger.info(thisStudent.getUsername() + " left group " + selectedGroup.getId());
		
		if(thisStudent.equals(selectedGroup.getOwner())) {
			if(selectedGroup.getMembers().size() > 1) {
				selectedGroup.setOwner(selectedGroup.getMembers().get(1));
				logger.info(selectedGroup.getOwner() + " is now the owner of group " + selectedGroup.getId());
			} else {
				logger.info("No members left in group " + selectedGroup.getId());
				onStudentGroupRemoved(selectedGroup);
				view.hideMembers();
				return;
			}
		}
		
		selectedGroup.removeMember(thisStudent);
		selectedGroup = dataService.saveStudentGroup(selectedGroup);
		selectedGroup.getMembers().forEach(s -> {
			UI ui = Application.getUserUI(s.getUsername());
			if(ui != null)
				ui.access(() -> ComponentUtil.fireEvent(ui, new StudentGroupUpdatedEvent(UI.getCurrent(), false)));
		});
		
		UI ui = Application.getUserUI("admin");
		if(ui != null)
			ui.access(() -> ComponentUtil.fireEvent(ui, new StudentGroupUpdatedEvent(UI.getCurrent(), false)));
		
		selectedGroup = null;
		view.hideMembers();
		updateGroupGrid();
	}

	@Override
	public void onGroupClick(StudentGroup studentGroup) {
		selectedGroup = studentGroup;
		
		List<Student> ls = studentGroup.getMembers();
		view.setMembersGridItems(ls);
		view.setMembersGridCount(ls.size());
		view.showMembers();
	}

	@Override
	public void onGroupDoubleClick(StudentGroup studentGroup) {
		if(!studentGroup.getOwner().getUsername().equals(securityService.getAuthenticatedUser().getUsername()))
			return;
		
		logger.info(securityService.getAuthenticatedUser().getUsername() + " clicked group " + studentGroup.getId());
		
		StudentGroupDialog studentGroupDialog = new StudentGroupDialog(dataService, securityService, studentGroup);
		studentGroupDialog.setOnSaveBiConsumer(this::onStudentGroupUpdated);
		studentGroupDialog.setOnRemoveConsumer(this::onStudentGroupRemoved);
		studentGroupDialog.open();		
	}

	@Override
	public void onMemberDoubleClick(Student clickedStudent) {
		if(clickedStudent.getUsername().equals(securityService.getAuthenticatedUser().getUsername())) {
			UI.getCurrent().navigate("profile/me");
		} else {
			UI.getCurrent().navigate("profile/" + clickedStudent.getUsername());
		}		
	}

	@Override
	public void createSession() {
		logger.info(securityService.getAuthenticatedUser().getUsername() + " is creating a session for group " + selectedGroup.getId());
		
		SessionDialog sessionDiag = new SessionDialog(dataService, securityService, null, selectedGroup);
		sessionDiag.setOnSaveBiConsumer(this::onSessionCreated);
		sessionDiag.open();			
	}
	

	private void onStudentGroupCreated(StudentGroup studentGroup, Set<Student> selectedStudents) {
		logger.info(securityService.getAuthenticatedUser().getUsername() + " created group " + studentGroup.getId());
		
		studentGroup.addMember(studentGroup.getOwner());
		dataService.saveStudentGroup(studentGroup);

		selectedStudents.forEach(s -> {
			StudentGroupRequest sgr = new StudentGroupRequest(studentGroup.getOwner().getUsername() + 
					" invited you to the group " + studentGroup.getName(), s, studentGroup.getId());
			final StudentGroupRequest persistentSGR = dataService.saveStudentGroupRequest(sgr);
			
			UI ui = Application.getUserUI(s.getUsername());
			if(ui != null)
				ui.access(() -> ComponentUtil.fireEvent(ui, new StudentGroupRequestCreatedEvent(UI.getCurrent(), false, persistentSGR)));
		});
		
		StringBuilder createLog = new StringBuilder();
		createLog.append("Group " + studentGroup.getId() + " created by " + studentGroup.getOwner() + ". Invites sent to: ");
		createLog.append(String.join(", ", selectedStudents.stream().map(Student::getUsername).toList()));
		
		logger.info(createLog.toString());
		
		UI ui = Application.getUserUI("admin");
		if(ui != null)
			ui.access(() -> ComponentUtil.fireEvent(ui, new StudentGroupUpdatedEvent(UI.getCurrent(), false)));

		updateGroupGrid();
	}

	private void onStudentGroupRemoved(StudentGroup studentGroup) {
		logger.info(securityService.getAuthenticatedUser().getUsername() + " removed group " + studentGroup.getId());
		
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
		
		UI ui = Application.getUserUI("admin");
		if(ui != null)
			ui.access(() -> ComponentUtil.fireEvent(ui, new StudentGroupRemovedEvent(UI.getCurrent(), false)));
	}

	private void onStudentGroupUpdated(StudentGroup studentGroup, Set<Student> unmodifiableSelectedStudents) {
		logger.info(securityService.getAuthenticatedUser().getUsername() + " updated group " + studentGroup.getId());
		
		Set<Student> oldParticipants = new HashSet<Student>(studentGroup.getMembers());
		Set<Student> selectedStudents = new HashSet<>(unmodifiableSelectedStudents);
		Set<Student> removedStudents = new HashSet<>();
		Set<Student> invitedStudents = new HashSet<>();
		
		studentGroup.getMembers().forEach(s -> {			
			if(s.getUsername().equals(securityService.getAuthenticatedUser().getUsername()))
				return;
			
			if(!selectedStudents.contains(s))
				removedStudents.add(s);
		});
		
		studentGroup.removeAllMembers();
		studentGroup.addMember(studentGroup.getOwner());
		
		selectedStudents.forEach(s -> {
			if(oldParticipants.contains(s)) {
				NotificationEntity n = new NotificationEntity(studentGroup.getOwner().getUsername() + 
						" made changes to one of your groups", s);
				final NotificationEntity persistentN = dataService.saveNotification(n);

				studentGroup.addMember(s);
				
				UI ui = Application.getUserUI(s.getUsername());
				if(ui != null)
					ui.access(() -> ComponentUtil.fireEvent(ui, new NotificationCreatedEvent(UI.getCurrent(), false, persistentN)));
				
			} else {
				invitedStudents.add(s);
				
				StudentGroupRequest sgr = new StudentGroupRequest(studentGroup.getOwner().getUsername() + 
						" invited you to the group " + studentGroup.getName(), s, studentGroup.getId());
				final StudentGroupRequest persistentSGR = dataService.saveStudentGroupRequest(sgr);
				
				UI ui = Application.getUserUI(s.getUsername());
				if(ui != null)
					ui.access(() -> ComponentUtil.fireEvent(ui, new StudentGroupRequestCreatedEvent(UI.getCurrent(), false, persistentSGR)));
			}
		});

		StudentGroup persistentStudentGroup = dataService.saveStudentGroup(studentGroup);
		
		StringBuilder updateLog = new StringBuilder();
		updateLog.append("Group " + persistentStudentGroup.getId() + " updated by " + persistentStudentGroup.getOwner() + ". Members: ");
		updateLog.append(String.join(", ", persistentStudentGroup.getMembers().stream().map(Student::getUsername).toList()));
		updateLog.append(". Removed students: ");
		updateLog.append(String.join(", ", removedStudents.stream().map(Student::getUsername).toList()));
		updateLog.append(". Invites sent to: ");
		updateLog.append(String.join(", ", invitedStudents.stream().map(Student::getUsername).toList()));
		
		logger.info(updateLog.toString());
		
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
		
		UI ui = Application.getUserUI("admin");
		if(ui != null)
			ui.access(() -> ComponentUtil.fireEvent(ui, new StudentGroupUpdatedEvent(UI.getCurrent(), false)));
	}
	
	private void onSessionCreated(Session session, Set<Student> selectedStudents) {
		logger.info(securityService.getAuthenticatedUser().getUsername() + " created session " + session.getId()
					+ " for group " + selectedGroup.getId());
		
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
		
		updateGroupGrid();
	}
}
