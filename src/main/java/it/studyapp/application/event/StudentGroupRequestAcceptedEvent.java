package it.studyapp.application.event;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.UI;

public class StudentGroupRequestAcceptedEvent extends ComponentEvent<UI> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public StudentGroupRequestAcceptedEvent(UI source, boolean fromClient) {
		super(source, fromClient);
	}

}
