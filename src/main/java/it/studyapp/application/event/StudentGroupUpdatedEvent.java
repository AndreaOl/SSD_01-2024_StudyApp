package it.studyapp.application.event;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.UI;

public class StudentGroupUpdatedEvent extends ComponentEvent<UI> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public StudentGroupUpdatedEvent(UI source, boolean fromClient) {
		super(source, fromClient);
	}

}
