package it.studyapp.application.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.UI;

import it.studyapp.application.entity.SessionRequest;


public class SessionRequestCreatedEvent extends ComponentEvent<UI> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private SessionRequest sessionRequest;
	
	private final Logger logger = LoggerFactory.getLogger(SessionRequestCreatedEvent.class);

	public SessionRequestCreatedEvent(UI source, boolean fromClient, SessionRequest sessionRequest) {
		super(source, fromClient);
		this.sessionRequest = sessionRequest;
		
		logger.info(source + " sent a session request. " + sessionRequest);
	}
	
	public SessionRequest getSessionRequest() {
		return this.sessionRequest;
	}

}
