package it.studyapp.application.presenter.profile;

import it.studyapp.application.entity.Student;

public interface ProfilePresenter {
	
	public void updateUser(Student user);
	public Student getAuthenticatedUser();
	public Student searchStudent(String username);
	public void resetPassword();
	
}
