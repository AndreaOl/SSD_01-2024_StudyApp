package it.studyapp.application.security;

import it.studyapp.application.security.vaadin.LogoutUtil;
import it.studyapp.application.service.CurrentSession;
import it.studyapp.application.service.UserInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Component
public class SecurityService {
	
	private final Logger logger = LoggerFactory.getLogger(SecurityService.class);

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
    	logger.info(currentSession.getCurrentUser().get().getUsername() + " logged out");
    	
    	logoutUtil.logout();
    }
    
    public boolean isAdmin() {
    	return currentSession.hasRole(Roles.ADMIN);
    }
}