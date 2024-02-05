package it.studyapp.application.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.UI;

import it.studyapp.application.entity.NotificationEntity;

public class NotificationCreatedEvent extends ComponentEvent<UI> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private NotificationEntity notification;
	
	private final Logger logger = LoggerFactory.getLogger(NotificationCreatedEvent.class);

	public NotificationCreatedEvent(UI source, boolean fromClient, NotificationEntity notification) {
		super(source, fromClient);
		this.notification = notification;
		
		logger.info(source + " sent a notification. " + notification);
	}
	
	public NotificationEntity getNotification() {
		return notification;
	}

}
