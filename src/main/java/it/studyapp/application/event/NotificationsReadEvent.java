package it.studyapp.application.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.UI;

public class NotificationsReadEvent extends ComponentEvent<UI> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L; 
	private final Logger logger = LoggerFactory.getLogger(NotificationsReadEvent.class);

	public NotificationsReadEvent(UI source, boolean fromClient) {
		super(source, fromClient);
		
		logger.info("All notifications read from " + source);
	}

}
