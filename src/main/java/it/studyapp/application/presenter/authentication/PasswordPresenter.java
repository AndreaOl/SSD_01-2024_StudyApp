package it.studyapp.application.presenter.authentication;

import it.studyapp.application.entity.Token;

public interface PasswordPresenter {
	
	public void changePassword(String oldPassword, String newPassword);
	public boolean passwordCheck(String password);
	public void restorePassword(String email, String newPassword);
	public Token searchToken(String randomToken);
	public void sendEmail(String email);

}
