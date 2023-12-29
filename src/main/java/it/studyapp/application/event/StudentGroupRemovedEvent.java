package it.studyapp.application.event;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.UI;

public class StudentGroupRemovedEvent extends ComponentEvent<UI> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public StudentGroupRemovedEvent(UI source, boolean fromClient) {
		super(source, fromClient);
	}

}
