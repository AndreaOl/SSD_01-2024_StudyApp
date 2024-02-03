package it.studyapp.application.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.UI;

import it.studyapp.application.entity.Reminder;


public class ReminderCreatedEvent extends ComponentEvent<UI> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Reminder reminder;
	
	private final Logger logger = LoggerFactory.getLogger(ReminderCreatedEvent.class);

	public ReminderCreatedEvent(UI source, boolean fromClient, Reminder reminder) {
		super(source, fromClient);
		this.reminder = reminder;
		
		logger.info(source + " created a reminder. " + reminder);
	}
	

	public Reminder getReminder() {
		return reminder;
	}

}
