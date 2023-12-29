package it.studyapp.application.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import it.studyapp.application.entity.Student;
import it.studyapp.application.presenter.authentication.RegisterPresenterImpl;
import it.studyapp.application.security.CustomUserDetails;
import it.studyapp.application.service.DataService;

@SpringBootTest
@TestExecutionListeners(listeners = DependencyInjectionTestExecutionListener.class,
	mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class RegisterPresenterTest {

	static {
		// Prevent Vaadin Development mode to launch browser window
		System.setProperty("vaadin.launch-browser", "false");
	}
	
	@Autowired
	private RegisterPresenterImpl presenter;

	@Autowired
	private DataService dataService;
	
	@Test
	public void registrazione_effettuata_correttamente() {
		CustomUserDetails user = new CustomUserDetails("mrossi", "Mario", "Rossi", "Ingegneria Informatica",
				LocalDate.now().minusYears(11), "2° Anno Magistrale", "mrossi@gmail.com", 0, "password");
		
		presenter.createUser(user);
		
		Student expectedStudent = new Student("mrossi", "Mario", "Rossi", "password", 
				LocalDate.now().minusYears(11), "Ingegneria Informatica", "2° Anno Magistrale", 
				"mrossi@gmail.com", 0, Arrays.asList("ROLE_USER"));
		
		Student persistentStudent = dataService.searchStudent("mrossi").get(0);
		
		assertStudentEquals(expectedStudent, persistentStudent);
	}
	
	@Test
	public void utente_creato_esiste() {
		CustomUserDetails user = new CustomUserDetails("mrossi", "Mario", "Rossi", "Ingegneria Informatica",
				LocalDate.now().minusYears(11), "2° Anno Magistrale", "mrossi@gmail.com", 0, "password");
		
		presenter.createUser(user);
		
		assertEquals(true, presenter.userExists("mrossi"));
		
		// Verifichiamo che un utente un salvato non venga trovato
		assertEquals(false, presenter.userExists("gespo"));
	}
	
	@Test
	public void email_salvata_esiste() {
		CustomUserDetails user = new CustomUserDetails("mrossi", "Mario", "Rossi", "Ingegneria Informatica",
				LocalDate.now().minusYears(11), "2° Anno Magistrale", "mrossi@gmail.com", 0, "password");
		
		presenter.createUser(user);
		
		assertEquals(true, presenter.emailExists("mrossi@gmail.com"));
		
		// Verifichiamo che un'email non salvata non venga trovata
		assertEquals(false, presenter.emailExists("gespo@gmail.com"));
	}
	
	private void assertStudentEquals(Student expectedStudent, Student actualStudent) {
		assertEquals(expectedStudent.getUsername(), actualStudent.getUsername());
		assertEquals(expectedStudent.getFirstName(), actualStudent.getFirstName());
		assertEquals(expectedStudent.getLastName(), actualStudent.getLastName());
		assertEquals(expectedStudent.getFieldOfStudy(), actualStudent.getFieldOfStudy());
		assertEquals(expectedStudent.getBirthDate(), actualStudent.getBirthDate());
		assertEquals(expectedStudent.getYearFollowing(), actualStudent.getYearFollowing());
		assertEquals(expectedStudent.getEmail(), actualStudent.getEmail());
		assertPasswordEquals(expectedStudent.getPassword(), actualStudent.getPassword());
		assertEquals(expectedStudent.getAvatar(), actualStudent.getAvatar());
		assertEquals(expectedStudent.getRoles(), actualStudent.getRoles());
	}
	
	private void assertPasswordEquals(String plainPassword, String cryptPassword) {
		PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
		assertEquals(true, encoder.matches(plainPassword, cryptPassword));
	}
}
