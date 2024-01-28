package it.studyapp.application.runnable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;

import it.studyapp.application.Application;
import it.studyapp.application.entity.Student;
import it.studyapp.application.entity.StudentGroup;
import it.studyapp.application.entity.StudentGroupRequest;
import it.studyapp.application.event.StudentGroupRequestAcceptedEvent;
import it.studyapp.application.security.SecurityService;
import it.studyapp.application.service.DataService;

public class StudentGroupRequestRunnable implements Runnable {
	
	private final Logger logger = LoggerFactory.getLogger(StudentGroupRequestRunnable.class);

	private DataService dataService;
	private SecurityService securityService;
	private StudentGroupRequest studentGroupRequest;

	public StudentGroupRequestRunnable(StudentGroupRequest studentGroupRequest, DataService dataService, SecurityService securityService) {
		this.dataService = dataService;
		this.securityService = securityService;
		this.studentGroupRequest = studentGroupRequest;
	}

	@Override
	public void run() {
		StudentGroup studentGroup = dataService.findStudentGroupById(studentGroupRequest.getStudentGroupId());
		Student notifiedStudent = dataService.searchStudent(securityService.getAuthenticatedUser().getUsername()).get(0);

		if(studentGroup != null && !studentGroup.getMembers().contains(notifiedStudent)) {
			studentGroup.addMember(notifiedStudent);
			dataService.saveStudentGroup(studentGroup);
			
			logger.info(notifiedStudent.getUsername() + " joined group " + studentGroup.getId());

			studentGroupRequest.setAccepted(Boolean.valueOf(true));
			dataService.saveStudentGroupRequest(studentGroupRequest);

			studentGroup.getMembers().forEach(s -> {
				UI ui = Application.getUserUI(s.getUsername());
				if(ui != null)
					ui.access(() -> ComponentUtil.fireEvent(ui, new StudentGroupRequestAcceptedEvent(UI.getCurrent(), false)));
			});
		}
	}
}
