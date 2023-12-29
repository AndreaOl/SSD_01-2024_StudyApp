package it.studyapp.application.presenter.profile;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import it.studyapp.application.entity.Student;
import it.studyapp.application.security.CustomUserDetails;
import it.studyapp.application.security.SecurityConfig;
import it.studyapp.application.security.SecurityService;
import it.studyapp.application.service.DataService;

@Component
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProfilePresenterImpl implements ProfilePresenter {
	
	@Autowired
	private SecurityConfig securityConfig;
	
	@Autowired
	private DataService dataService;
	
	@Autowired
	private SecurityService securityService;
	
	@Override
	public void updateUser(CustomUserDetails user) {
		securityConfig.getManager().updateUser(user);
	}
	
	@Override
	public Student getAuthenticatedUser() {
		return dataService.searchStudent(securityService.getAuthenticatedUser().getUsername()).get(0);
	}

	@Override
	public Student searchStudent(String username) {
		List<Student> studentList = dataService.searchStudent(username);
		
		if(studentList == null || studentList.isEmpty())
			return null;
		
		return studentList.get(0);
	}

}
