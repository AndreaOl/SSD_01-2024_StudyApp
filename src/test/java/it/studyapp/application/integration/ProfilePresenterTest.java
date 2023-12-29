package it.studyapp.application.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import it.studyapp.application.entity.Student;
import it.studyapp.application.presenter.profile.ProfilePresenterImpl;
import it.studyapp.application.security.CustomUserDetails;
import it.studyapp.application.service.DataService;

@SpringBootTest
@AutoConfigureMockMvc
@TestExecutionListeners(listeners = DependencyInjectionTestExecutionListener.class,
	mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ProfilePresenterTest {

	static {
		// Prevent Vaadin Development mode to launch browser window
		System.setProperty("vaadin.launch-browser", "false");
	}
	
	private Student user;
	
	@Autowired
	private ProfilePresenterImpl presenter;
	
	@Autowired
	private DataService dataService;
	
	@BeforeEach
	public void setup() {
		user = new Student("mrossi", "Mario", "Rossi", "{bcrypt}$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxO/BTk76klW", 
				LocalDate.now(), "Ingegneria Informatica", "2° Anno Magistrale", "mrossi@gmail.com", 0, Arrays.asList("ROLE_USER"));
		
		user = dataService.saveStudent(user);
	}
	
	@Test
	public void profilo_utente_aggiornato_correttamente() {
		CustomUserDetails userDetails = new CustomUserDetails(user);
		
		// Aggiorniamo i campi dell'utente
		userDetails.setFirstName("Giuseppe");
		userDetails.setLastName("Esposito");
		userDetails.setBirthDate(LocalDate.of(1990, 10, 20));
		userDetails.setFieldOfStudy("Ingegneria Meccanica");
		userDetails.setAvatar(1);
		userDetails.setYearFollowing("1° Anno Triennale");
		
		presenter.updateUser(userDetails);
		
		Student expectedStudent = new Student(userDetails);
		
		Student persistentStudent = presenter.searchStudent(userDetails.getUsername());
		
		// Verifichiamo che il profilo utente sia stato aggiornato correttamente
		assertStudentEquals(expectedStudent, persistentStudent);
	}
	
	@Test
	@WithMockUser(username = "mrossi", password = "password", roles = "USER")
	public void utente_autenticato_correttamente_recuperato() {
		Student persistentUser = presenter.getAuthenticatedUser();
		
		assertStudentEquals(user, persistentUser);
	}
	
	private void assertStudentEquals(Student expectedStudent, Student actualStudent) {
		assertEquals(expectedStudent.getUsername(), actualStudent.getUsername());
		assertEquals(expectedStudent.getFirstName(), actualStudent.getFirstName());
		assertEquals(expectedStudent.getLastName(), actualStudent.getLastName());
		assertEquals(expectedStudent.getFieldOfStudy(), actualStudent.getFieldOfStudy());
		assertEquals(expectedStudent.getBirthDate(), actualStudent.getBirthDate());
		assertEquals(expectedStudent.getYearFollowing(), actualStudent.getYearFollowing());
		assertEquals(expectedStudent.getEmail(), actualStudent.getEmail());
		assertEquals(expectedStudent.getPassword(), actualStudent.getPassword());
		assertEquals(expectedStudent.getAvatar(), actualStudent.getAvatar());
		assertEquals(expectedStudent.getRoles(), actualStudent.getRoles());
	}
}
