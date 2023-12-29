package it.studyapp.application.event;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.UI;

public class ProfileUpdatedEvent extends ComponentEvent<UI> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String username;

	public ProfileUpdatedEvent(UI source, boolean fromClient, String username) {
		super(source, fromClient);
		this.username = username;
	}
	
	public String getUsername() {
		return username;
	}

}
