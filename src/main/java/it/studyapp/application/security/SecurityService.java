package it.studyapp.application.security;

import it.studyapp.application.security.vaadin.LogoutUtil;
import it.studyapp.application.service.CurrentSession;
import it.studyapp.application.service.UserInfo;

import org.springframework.stereotype.Component;


@Component
public class SecurityService {

    private final CurrentSession currentSession;
    private final LogoutUtil logoutUtil;

    public SecurityService(CurrentSession currentSession, LogoutUtil logoutUtil) {
        this.currentSession = currentSession;
        this.logoutUtil = logoutUtil;
    }
    
    public UserInfo getAuthenticatedUser() {
        return currentSession.getCurrentUser().get();
    }
    
    public boolean isAuthenticated() {
    	return currentSession.getCurrentUser().isPresent();
    }

    public void logout() {
    	logoutUtil.logout();
    }
    
    public boolean isAdmin() {
    	return currentSession.hasRole(Roles.ADMIN);
    }
}