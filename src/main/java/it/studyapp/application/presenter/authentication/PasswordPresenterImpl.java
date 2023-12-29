package it.studyapp.application.presenter.authentication;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import it.studyapp.application.entity.Token;
import it.studyapp.application.security.CustomUserDetails;
import it.studyapp.application.security.SecurityConfig;
import it.studyapp.application.security.SecurityService;
import it.studyapp.application.service.DataService;

@Component
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PasswordPresenterImpl implements PasswordPresenter {
	
	@Autowired
	private DataService dataService;
	
	@Autowired
	private SecurityService securityService;
	
	@Autowired
	private SecurityConfig securityConfig;
	
	@Autowired
	private MailSender mailSender;

	private PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
	
	@Override
	public void changePassword(String oldPassword, String newPassword) {
		securityConfig.getManager().changePassword(oldPassword, encoder.encode(newPassword));
	}

	@Override
	public boolean passwordCheck(String password) {
		return securityConfig.getManager().passwordCheck(securityService.getAuthenticatedUser().getUsername(), password);
	}

	@Override
	public void restorePassword(String email, String newPassword) {
		CustomUserDetails user = new CustomUserDetails();
		List<Token> tokenListEmail = dataService.searchTokenEmail(email);
		if(!tokenListEmail.isEmpty()) {		
			user.setUsername(dataService.searchStudentsEmail(email).get(0).getUsername());
			securityConfig.getManager().updatePassword(user, encoder.encode(newPassword));
			dataService.deleteToken(tokenListEmail.get(0));
		}		
	}
	
	@Override
	public Token searchToken(String randomToken) {
		List<Token> tokenList = dataService.searchToken(randomToken);

		if(tokenList == null || tokenList.isEmpty())
			return null;

		return tokenList.get(0);
	}

	@Override
	public void sendEmail(String email) {
		if(!securityConfig.getManager().emailExists(email))
			return;
		
		List<Token> tokenList = dataService.searchTokenEmail(email);

		UUID uuid = UUID.randomUUID();
		String token = uuid.toString().replaceAll("-", "");
		String text = "Cambia la tua password al seguente link: \n";
		String link = "http://studyapp.northeurope.cloudapp.azure.com:8080/forgot/" + token;
		
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(email);
		message.setFrom("noreply@studyapp.com");
		message.setSubject("Cambia la tua password");
		message.setText(text + link);

		if(tokenList != null && !tokenList.isEmpty() ) {
			Token tokenFound = tokenList.get(0);
			tokenFound.setRandomToken(token);
			dataService.updateToken(tokenFound);
		} else {
			Token newToken = new Token(token, email);
			dataService.saveToken(newToken);
		}

		mailSender.send(message);				
	}

}
