package it.studyapp.application.presenter.profile;

import it.studyapp.application.entity.Student;
import it.studyapp.application.security.CustomUserDetails;

public interface ProfilePresenter {
	
	public void updateUser(CustomUserDetails user);
	public Student getAuthenticatedUser();
	public Student searchStudent(String username);
	
}
