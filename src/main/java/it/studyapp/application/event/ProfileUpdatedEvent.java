package it.studyapp.application.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.UI;

public class ProfileUpdatedEvent extends ComponentEvent<UI> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String username;
	
	private final Logger logger = LoggerFactory.getLogger(ProfileUpdatedEvent.class);

	public ProfileUpdatedEvent(UI source, boolean fromClient, String username) {
		super(source, fromClient);
		this.username = username;
		
		logger.info(source + ": profile updated for user " + username);
	}
	
	public String getUsername() {
		return username;
	}

}
