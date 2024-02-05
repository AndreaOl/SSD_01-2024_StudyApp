package it.studyapp.application.runnable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;

import it.studyapp.application.Application;
import it.studyapp.application.entity.Session;
import it.studyapp.application.entity.SessionRequest;
import it.studyapp.application.entity.Student;
import it.studyapp.application.event.SessionRequestAcceptedEvent;
import it.studyapp.application.security.SecurityService;
import it.studyapp.application.service.DataService;

public class SessionRequestRunnable implements Runnable {
	
	private final Logger logger = LoggerFactory.getLogger(SessionRequestRunnable.class);
	
	private DataService dataService;
	private SecurityService securityService;
	private SessionRequest sessionRequest;
	
	public SessionRequestRunnable(SessionRequest sessionRequest, DataService dataService, SecurityService securityService) {
		this.dataService = dataService;
		this.securityService = securityService;
		this.sessionRequest = sessionRequest;
	}

	@Override
	public void run() {
		Session session = dataService.findSessionById(sessionRequest.getSessionId());
		Student notifiedStudent = dataService.searchStudent(securityService.getAuthenticatedUser().getUsername()).get(0);
		
		if(session != null && !session.getParticipants().contains(notifiedStudent)) {		
			session.addParticipant(notifiedStudent);
			dataService.saveSession(session);
			
			logger.info(notifiedStudent.getUsername() + " joined session " + session.getId());
	        
	        sessionRequest.setAccepted(Boolean.valueOf(true));
	        dataService.saveSessionRequest(sessionRequest);
	        	        
	        session.getParticipants().forEach(s -> {
	        	UI ui = Application.getUserUI(s.getUsername());
		        if(ui != null)
		        	ui.access(() -> ComponentUtil.fireEvent(ui, new SessionRequestAcceptedEvent(UI.getCurrent(), false)));
	        });
		}		
	}
}
