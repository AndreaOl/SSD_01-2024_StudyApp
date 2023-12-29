package it.studyapp.application.presenter.authentication;

import it.studyapp.application.security.CustomUserDetails;

public interface RegisterPresenter {
	
	public void createUser(CustomUserDetails user);
	public boolean userExists(String username);
	public boolean emailExists(String email);
	
}