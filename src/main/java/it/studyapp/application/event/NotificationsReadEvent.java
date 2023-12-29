package it.studyapp.application.event;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.UI;

public class NotificationsReadEvent extends ComponentEvent<UI> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NotificationsReadEvent(UI source, boolean fromClient) {
		super(source, fromClient);
	}

}
