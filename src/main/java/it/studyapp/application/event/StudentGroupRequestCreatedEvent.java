package it.studyapp.application.event;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.UI;

import it.studyapp.application.entity.StudentGroupRequest;


public class StudentGroupRequestCreatedEvent extends ComponentEvent<UI> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private StudentGroupRequest studentGroupRequest;

	public StudentGroupRequestCreatedEvent(UI source, boolean fromClient, StudentGroupRequest studentGroupRequest) {
		super(source, fromClient);
		this.studentGroupRequest = studentGroupRequest;
	}
	
	public StudentGroupRequest getStudentGroupRequest() {
		return studentGroupRequest;
	}

}
