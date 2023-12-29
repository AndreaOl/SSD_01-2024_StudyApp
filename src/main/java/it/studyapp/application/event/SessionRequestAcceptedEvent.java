package it.studyapp.application.event;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.UI;

public class SessionRequestAcceptedEvent extends ComponentEvent<UI> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SessionRequestAcceptedEvent(UI source, boolean fromClient) {
		super(source, fromClient);
	}

}
