package it.studyapp.application.security;

import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;

/**
 * {@link HttpSessionListener} that updates a {@link HttpSessionRepository} when sessions are created and destroyed.
 */
public class HttpSessionRepositoryListener implements HttpSessionListener {

    private final HttpSessionRepository sessionRepository;

    public HttpSessionRepositoryListener(HttpSessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        sessionRepository.add(se.getSession());
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        sessionRepository.remove(se.getSession());
    }
}
