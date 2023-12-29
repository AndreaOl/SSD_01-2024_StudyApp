package it.studyapp.application.event;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.UI;

import it.studyapp.application.entity.SessionRequest;


public class SessionRequestCreatedEvent extends ComponentEvent<UI> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private SessionRequest sessionRequest;

	public SessionRequestCreatedEvent(UI source, boolean fromClient, SessionRequest sessionRequest) {
		super(source, fromClient);
		this.sessionRequest = sessionRequest;
	}
	
	public SessionRequest getSessionRequest() {
		return this.sessionRequest;
	}

}
