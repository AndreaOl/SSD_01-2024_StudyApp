package it.studyapp.application.security;

import java.util.Collection;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.log.LogMessage;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.util.Assert;

import it.studyapp.application.entity.Student;
import it.studyapp.application.service.DataService;

public class PersistentUserDetailsManager implements UserDetailsManager, UserDetailsPasswordService {

	protected final Log logger = LogFactory.getLog(getClass());
	//Sostituisci con utenti database
	//private final Map<String, MutableUserDetails> users = new HashMap<>();

	private SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder
			.getContextHolderStrategy();

	private AuthenticationManager authenticationManager;
	
	private DataService dataService;
	
	private PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();


	public PersistentUserDetailsManager(DataService dataService) {
		this.dataService = dataService;
	}

	public PersistentUserDetailsManager(DataService dataService, Collection<UserDetails> users) {
		this.dataService = dataService;
		for (UserDetails user : users) {
			if(!userExists(user.getUsername()))
				createUser(user);
		}
	}

	public PersistentUserDetailsManager(DataService dataService, UserDetails... users) {
		this.dataService = dataService;
		for (UserDetails user : users) {
			if(!userExists(user.getUsername()))
				createUser(user);
		}
	}

	@Override
	public void createUser(UserDetails user) {
		Assert.isTrue(!userExists(user.getUsername()), "user should not exist");
		//this.users.put(user.getUsername().toLowerCase(), new MutableUser(user));
		Student student = new Student((CustomUserDetails) user);
		dataService.saveStudent(student);
	}

	@Override
	public void deleteUser(String username) {
		//Student student= new Student((CustomUserDetails) user);
		//dataService.saveStudent(student);
		//this.users.remove(username.toLowerCase());

		List<Student> databaseEntries = dataService.searchStudent(username);
		if(databaseEntries != null && !databaseEntries.isEmpty()) {
			dataService.deleteStudent(databaseEntries.get(0));
		};
	}

	@Override
	public void updateUser(UserDetails user) {
		List<Student> databaseEntries = dataService.searchStudent(user.getUsername());
		if(databaseEntries != null && !databaseEntries.isEmpty()) {
			dataService.updateStudent(new Student((CustomUserDetails)user));
		};
	}

	@Override
	public boolean userExists(String username) {
		List<Student> databaseEntries = dataService.searchStudent(username);
		return (databaseEntries != null && !databaseEntries.isEmpty());
	}

	public boolean emailExists(String email) {
		List<Student> databaseEntries = dataService.searchStudentsEmail(email);
		return (databaseEntries != null && !databaseEntries.isEmpty());
	}
	
	public CustomUserDetails emailtoUserDetails(String email) {
		List<Student> databaseEntries = dataService.searchStudentsEmail(email);
		CustomUserDetails custom_user= new CustomUserDetails(databaseEntries.get(0));
		return custom_user;
	}
	
	@Override
	public void changePassword(String oldPassword, String newPassword) {
		Authentication currentUser = this.securityContextHolderStrategy.getContext().getAuthentication();
		if (currentUser == null) {
			// This would indicate bad coding somewhere
			throw new AccessDeniedException(
					"Can't change password as no Authentication object found in context " + "for current user.");
		}
		String username = currentUser.getName();
		this.logger.debug(LogMessage.format("Changing password for user '%s'", username));
		// If an authentication manager has been set, re-authenticate the user with the
		// supplied password.
		if (this.authenticationManager != null) {
			this.logger.debug(LogMessage.format("Reauthenticating user '%s' for password change request.", username));
			this.authenticationManager
					.authenticate(UsernamePasswordAuthenticationToken.unauthenticated(username, oldPassword));
		}
		else {
			this.logger.debug("No authentication manager set. Password won't be re-checked.");
		}
		List<Student> databaseEntries = dataService.searchStudent(username);
		if(databaseEntries != null && !databaseEntries.isEmpty()) {
			databaseEntries.get(0).setPassword(newPassword);
			dataService.changeStudentPassword(databaseEntries.get(0));
		};
		//MutableUserDetails user = this.users.get(username);
		//Assert.state(user != null, "Current user doesn't exist in database.");
		//user.setPassword(newPassword);
	}
	
	public boolean passwordCheck(String username, String password) {
		List<Student> databaseEntries = dataService.searchStudent(username);
		if(databaseEntries != null && !databaseEntries.isEmpty()) {
			return encoder.matches(password, databaseEntries.get(0).getPassword());
		}
		return false;
	}

	@Override
	public UserDetails updatePassword(UserDetails user, String newPassword) {
		List<Student> databaseEntries = dataService.searchStudent(user.getUsername());
		if(databaseEntries == null || databaseEntries.isEmpty()) {
			return null;
		}
		databaseEntries.get(0).setPassword(newPassword);
		dataService.changeStudentPassword(databaseEntries.get(0));
		CustomUserDetails custom_user= new CustomUserDetails(databaseEntries.get(0));
		return custom_user;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		List<Student> databaseEntries = dataService.searchStudent(username);
		if(databaseEntries == null || databaseEntries.isEmpty()) {
			throw new UsernameNotFoundException(username);
		};
		CustomUserDetails custom_user= new CustomUserDetails(databaseEntries.get(0));
		
		return custom_user;
	}

	/**
	 * Sets the {@link SecurityContextHolderStrategy} to use. The default action is to use
	 * the {@link SecurityContextHolderStrategy} stored in {@link SecurityContextHolder}.
	 *
	 * @since 5.8
	 */
	public void setSecurityContextHolderStrategy(SecurityContextHolderStrategy securityContextHolderStrategy) {
		Assert.notNull(securityContextHolderStrategy, "securityContextHolderStrategy cannot be null");
		this.securityContextHolderStrategy = securityContextHolderStrategy;
	}

	public void setAuthenticationManager(AuthenticationManager authenticationManager) {
		this.authenticationManager = authenticationManager;
	}


}
