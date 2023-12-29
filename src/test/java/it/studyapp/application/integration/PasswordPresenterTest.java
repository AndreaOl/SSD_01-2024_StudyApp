package it.studyapp.application.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;

import java.util.Arrays;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import it.studyapp.application.entity.Student;
import it.studyapp.application.entity.Token;
import it.studyapp.application.presenter.authentication.PasswordPresenterImpl;
import it.studyapp.application.service.DataService;

@SpringBootTest
@AutoConfigureMockMvc
@TestExecutionListeners(listeners = DependencyInjectionTestExecutionListener.class,
	mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class PasswordPresenterTest {

	static {
		// Prevent Vaadin Development mode to launch browser window
		System.setProperty("vaadin.launch-browser", "false");
	}
	
	@Autowired
	private PasswordPresenterImpl presenter;

	@Autowired
	private DataService dataService;

	private Student user;
	
	@BeforeEach
	public void setup() {
		user = new Student("mrossi", "Mario", "Rossi", "{bcrypt}$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxO/BTk76klW", 
				LocalDate.now(), "Ingegneria Informatica", "2° Anno Magistrale", "mrossi@gmail.com", 0, Arrays.asList("ROLE_USER"));
		
		user = dataService.saveStudent(user);
	}
	
	@Test
	@WithMockUser(username = "mrossi", password = "password", roles = "USER")
	public void password_cambiata_correttamente() {
		
		presenter.changePassword("password", "newpassword");
		
		String expectedPassword = "newpassword";
		
		String persistentPassword = dataService.searchStudent("mrossi").get(0).getPassword();
		
		// Verifichiamo che la password sia stata cambiata correttamente
		assertPasswordEquals(expectedPassword, persistentPassword);		
	}
	
	@Test
	@WithMockUser(username = "mrossi", password = "password", roles = "USER")
	public void controllo_password() {
		
		// Controlli sull'utente autenticato (mrossi)
		Boolean checkTrueResult = presenter.passwordCheck("password");
		Boolean checkFalseResult = presenter.passwordCheck("notpassword");
		
		// Verifichiamo che sia riconosciuta la password corretta
		assertEquals(true, checkTrueResult);
		
		// Verifichiamo che la password errata non sia riconosciuta
		assertEquals(false, checkFalseResult);
	}
	
	@Test
	@WithMockUser(username = "mrossi", password = "password", roles = "USER")
	public void password_cambiata_tramite_token() {
		UUID uuid = UUID.randomUUID();
		String token_string = uuid.toString().replaceAll("-", "");
		
		String userEmail = "mrossi@gmail.com";
		
		Token token = new Token(token_string, userEmail);
		dataService.saveToken(token);
		
		String expectedPassword = "newpassword";
		presenter.restorePassword(userEmail, expectedPassword);
		
		String persistentPassword = dataService.searchStudent("mrossi").get(0).getPassword();
		
		assertPasswordEquals(expectedPassword, persistentPassword);			
	}
		
	@Test
	@WithMockUser(username = "mrossi", password = "password", roles = "USER")
	public void token_correttamente_eliminato() {
		
		UUID uuid = UUID.randomUUID();
		String token_string = uuid.toString().replaceAll("-", "");
		
		String userEmail = "mrossi@gmail.com";
		
		Token token = new Token(token_string, userEmail);
		token = dataService.saveToken(token);
		
		Token persistentToken = presenter.searchToken(token_string);
		
		// Verifichiamo che il token sia stato salvato e trovato correttamente
		assertTokenEquals(token, persistentToken);

		presenter.restorePassword(userEmail, "newpassword");
		
		persistentToken = presenter.searchToken(userEmail);
		
		// Verifichiamo che il token sia stato eliminato dopo aver resettato la password
		assertEquals(null, persistentToken);
	}
	
	private void assertPasswordEquals(String plainPassword, String cryptPassword) {
		PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
		assertEquals(true, encoder.matches(plainPassword, cryptPassword));
	}

	private void assertTokenEquals(Token expectedToken, Token actualToken) {
		assertEquals(expectedToken.getRandomToken(), actualToken.getRandomToken());
		assertEquals(expectedToken.getEmail(), actualToken.getEmail());
	}
}
