package it.studyapp.application.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;

/**
 * {@link HttpSessionListener} that updates a {@link HttpSessionRepository} when sessions are created and destroyed.
 */
public class HttpSessionRepositoryListener implements HttpSessionListener {

    private final HttpSessionRepository sessionRepository;
    
    private final Logger logger = LoggerFactory.getLogger(HttpSessionRepositoryListener.class);

    public HttpSessionRepositoryListener(HttpSessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    @Override
    public void sessionCreated(HttpSessionEvent se) {
    	logger.info("New HTTP session created. ID: " + se.getSession().getId());
    	
        sessionRepository.add(se.getSession());
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
    	logger.info("HTTP session " + se.getSession().getId() + " destroyed.");
    	
        sessionRepository.remove(se.getSession());
    }
}
