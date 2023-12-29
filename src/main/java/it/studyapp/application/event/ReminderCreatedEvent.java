package it.studyapp.application.event;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.UI;

import it.studyapp.application.entity.Reminder;


public class ReminderCreatedEvent extends ComponentEvent<UI> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Reminder reminder;

	public ReminderCreatedEvent(UI source, boolean fromClient, Reminder reminder) {
		super(source, fromClient);
		this.reminder = reminder;
	}
	

	public Reminder getReminder() {
		return reminder;
	}

}
