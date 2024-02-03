package it.studyapp.application.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.UI;

import it.studyapp.application.entity.StudentGroupRequest;


public class StudentGroupRequestCreatedEvent extends ComponentEvent<UI> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private StudentGroupRequest studentGroupRequest;
	
	private final Logger logger = LoggerFactory.getLogger(StudentGroupRequestCreatedEvent.class);

	public StudentGroupRequestCreatedEvent(UI source, boolean fromClient, StudentGroupRequest studentGroupRequest) {
		super(source, fromClient);
		this.studentGroupRequest = studentGroupRequest;
		
		logger.info(source + " sent a student group request. " + studentGroupRequest);
	}
	
	public StudentGroupRequest getStudentGroupRequest() {
		return studentGroupRequest;
	}

}
