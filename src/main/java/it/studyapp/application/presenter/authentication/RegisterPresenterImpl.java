package it.studyapp.application.presenter.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import it.studyapp.application.security.CustomUserDetails;
import it.studyapp.application.security.SecurityConfig;

@Component
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class RegisterPresenterImpl implements RegisterPresenter {
	
	@Autowired
	private SecurityConfig securityConfig;
	
	@Override
	public void createUser(CustomUserDetails user) {
		PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
		user.setPassword(encoder.encode(user.getPassword()));
		securityConfig.getManager().createUser(user);
	}
	
	@Override
	public boolean userExists(String username) {
		return securityConfig.getManager().userExists(username);
	}
	
	@Override
	public boolean emailExists(String email) {
		return securityConfig.getManager().emailExists(email);
	}
}
