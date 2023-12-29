package it.studyapp.application.event;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.UI;

import it.studyapp.application.entity.NotificationEntity;

public class NotificationCreatedEvent extends ComponentEvent<UI> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private NotificationEntity notification;

	public NotificationCreatedEvent(UI source, boolean fromClient, NotificationEntity notification) {
		super(source, fromClient);
		this.notification = notification;
	}
	
	public NotificationEntity getNotification() {
		return notification;
	}

}
