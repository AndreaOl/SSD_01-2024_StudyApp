package it.studyapp.application.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

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
import it.studyapp.application.entity.StudentGroup;
import it.studyapp.application.entity.StudentGroupRequest;
import it.studyapp.application.presenter.group.GroupPresenterImpl;
import it.studyapp.application.service.DataService;
import it.studyapp.application.view.group.GroupView;

@SpringBootTest
@AutoConfigureMockMvc
@TestExecutionListeners(listeners = DependencyInjectionTestExecutionListener.class,
	mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class GroupPresenterTest {

	static {
		// Prevent Vaadin Development mode to launch browser window
		System.setProperty("vaadin.launch-browser", "false");
	}

	private Student owner;
	private Student member;
	private GroupView dummyView;

	@Autowired
	private GroupPresenterImpl presenter;

	@Autowired
	private DataService dataService;

	@BeforeEach
	public void setup() {
		owner = new Student("mrossi", "Mario", "Rossi", "{bcrypt}$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxO/BTk76klW", 
				LocalDate.now(), "Ingegneria Informatica", "2° Anno Magistrale", "mrossi@gmail.com", 0, Arrays.asList("ROLE_USER"));

		member = new Student("gespo", "Giuseppe", "Esposito", "{bcrypt}$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxO/BTk76klW", 
				LocalDate.now(), "Ingegneria Informatica", "2° Anno Magistrale", "gespo@gmail.com", 0, Arrays.asList("ROLE_USER"));

		owner = dataService.saveStudent(owner);

		member = dataService.saveStudent(member);

		dummyView = new GroupView() {
			@Override
			public void setGroupGridItems(List<StudentGroup> items) {}
			@Override
			public void setGroupGridCount(int count) {}
			@Override
			public void setMembersGridItems(List<Student> items) {}
			@Override
			public void setMembersGridCount(int count) {}
			@Override
			public void showMembers() {}
			@Override
			public void hideMembers() {}
		};

		presenter.setView(dummyView);
	}
	
	@Test
	@WithMockUser(username = "mrossi", password = "password", roles = "USER")
	public void gruppo_creato_correttamente() throws InvocationTargetException, 
	IllegalAccessException, NoSuchMethodException {

		// Nuovo gruppo senza membri ma con l'owner
		StudentGroup studentGroup = new StudentGroup("SADGroup", owner, new ArrayList<>());
				
		// Il presenter salva il gruppo e richieste relative ad esso
		getOnStudentGroupCreatedMethod(presenter).invoke(presenter, studentGroup, Set.of(member));

		StudentGroup persistentStudentGroup = dataService.findStudentGroupById(studentGroup.getId());
		assertStudentGroupEquals(studentGroup, persistentStudentGroup);
	}
	
	@Test
	@WithMockUser(username = "mrossi", password = "password", roles = "USER")
	public void owner_ha_il_gruppo_creato() throws InvocationTargetException, 
	IllegalAccessException, NoSuchMethodException {
		
		// Nuovo gruppo senza membri ma con l'owner
		StudentGroup studentGroup = new StudentGroup("SADGroup", owner, new ArrayList<>());
		
		// Il presenter salva il gruppo e richieste relative ad esso
		getOnStudentGroupCreatedMethod(presenter).invoke(presenter, studentGroup, Set.of(member));
		
		// Recuperiamo il riferimento aggiornato dell'owner dal database
		Student persistentOwner = dataService.findStudentById(owner.getId());

		// Verifichiamo che l'owner abbia il gruppo corretto
		assertStudentGroupEquals(studentGroup, persistentOwner.getStudentGroups().get(0));
	}
	
	@Test
	@WithMockUser(username = "mrossi", password = "password", roles = "USER")
	public void richiesta_gruppo_creata_correttamente() throws InvocationTargetException, 
	IllegalAccessException, NoSuchMethodException {
		
		// Nuovo gruppo senza membri ma con l'owner
		StudentGroup studentGroup = new StudentGroup("SADGroup", owner, new ArrayList<>());

		// Il presenter salva il gruppo e richieste relative ad esso
		getOnStudentGroupCreatedMethod(presenter).invoke(presenter, studentGroup, Set.of(member));
		
		// Richiesta di partecipazione al grouppo associato al membro
		StudentGroupRequest expectedStudentGroupRequest = new StudentGroupRequest(studentGroup.getOwner().getUsername() + 
				" invited you to the group " + studentGroup.getName(), member, studentGroup.getId());
		
		Student persistentMember = dataService.findStudentById(member.getId());
		
		assertStudentGroupRequestEquals(expectedStudentGroupRequest, 
				(StudentGroupRequest) persistentMember.getNotifications().get(0));
	}
	
	@Test
	@WithMockUser(username = "mrossi", password = "password", roles = "USER")
	public void member_non_ha_gruppo_alla_creazione() throws InvocationTargetException, 
	IllegalAccessException, NoSuchMethodException {
		
		// Nuovo gruppo senza membri ma con l'owner
		StudentGroup studentGroup = new StudentGroup("SADGroup", owner, new ArrayList<>());

		// Il presenter salva il gruppo e richieste relative ad esso
		getOnStudentGroupCreatedMethod(presenter).invoke(presenter, studentGroup, Set.of(member));
		
		Student persistentMember = dataService.findStudentById(member.getId());
		
		// Verifichiamo che il membro non faccia ancora parte del gruppo
		assertEquals(0, persistentMember.getStudentGroups().size());
	}
	
	@Test
	@WithMockUser(username = "mrossi", password = "password", roles = "USER")
	public void gruppi_rimosso_correttamente() throws InvocationTargetException, 
	IllegalAccessException, NoSuchMethodException {
		
		// Nuovo gruppo senza membri ma con l'owner
		StudentGroup studentGroup = new StudentGroup("SADGroup", owner, new ArrayList<>());

		// Il presenter salva il gruppo e richieste relative ad esso
		getOnStudentGroupCreatedMethod(presenter).invoke(presenter, studentGroup, Set.of(member));

		// Recuperiamo il riferimento aggiornato del gruppo
		StudentGroup persistentStudentGroup = dataService.findStudentGroupById(studentGroup.getId());

		// Il presenter elimina gruppo e richieste relative ad esso
		getOnStudentGroupRemovedMethod(presenter).invoke(presenter, persistentStudentGroup);
		
		// Recuperiamo il riferimento aggiornato del gruppo
		persistentStudentGroup = dataService.findStudentGroupById(studentGroup.getId());

		// Verifichiamo che il gruppo non esista più
		assertEquals(null, persistentStudentGroup);
	}
	
	@Test
	@WithMockUser(username = "mrossi", password = "password", roles = "USER")
	public void owner_non_ha_gruppo_dopo_rimozione() throws InvocationTargetException, 
	IllegalAccessException, NoSuchMethodException {
		
		// Nuovo gruppo senza membri ma con l'owner
		StudentGroup studentGroup = new StudentGroup("SADGroup", owner, new ArrayList<>());

		// Il presenter salva il gruppo e richieste relative ad esso
		getOnStudentGroupCreatedMethod(presenter).invoke(presenter, studentGroup, Set.of(member));

		// Recuperiamo il riferimento aggiornato del gruppo
		StudentGroup persistentStudentGroup = dataService.findStudentGroupById(studentGroup.getId());

		// Il presenter elimina gruppo e richieste relative ad esso
		getOnStudentGroupRemovedMethod(presenter).invoke(presenter, persistentStudentGroup);
		
		// Recuperiamo il riferimento aggiornato dell'owner dal database
		Student persistentOwner = dataService.findStudentById(owner.getId());

		// Verifichiamo che l'owner non abbia piu il gruppo
		assertEquals(0, persistentOwner.getStudentGroups().size());
	}
	
	@Test
	@WithMockUser(username = "mrossi", password = "password", roles = "USER")
	public void member_non_ha_richiesta_dopo_rimozione_gruppo() throws InvocationTargetException, 
	IllegalAccessException, NoSuchMethodException {
		
		// Nuovo gruppo senza membri ma con l'owner
		StudentGroup studentGroup = new StudentGroup("SADGroup", owner, new ArrayList<>());

		// Il presenter salva il gruppo e richieste relative ad esso
		getOnStudentGroupCreatedMethod(presenter).invoke(presenter, studentGroup, Set.of(member));

		// Recuperiamo il riferimento aggiornato del gruppo
		StudentGroup persistentStudentGroup = dataService.findStudentGroupById(studentGroup.getId());

		// Il presenter elimina gruppo e richieste relative ad esso
		getOnStudentGroupRemovedMethod(presenter).invoke(presenter, persistentStudentGroup);
		
		// Recuperiamo il riferimento aggiornato del participant dal database
		Student persistentMember = dataService.findStudentById(member.getId());

		// Verifichiamo che il participant non abbia più richieste
		assertEquals(0, persistentMember.getNotifications().size());
	}
	
	@Test
	@WithMockUser(username = "mrossi", password = "password", roles = "USER")
	public void nome_gruppo_aggiornato_correttamente() throws InvocationTargetException, 
	IllegalAccessException, NoSuchMethodException {
		
		// Nuovo gruppo senza membri ma con l'owner
		StudentGroup studentGroup = new StudentGroup("SADGroup", owner, new ArrayList<>());

		// Il presenter salva il gruppo e richieste relative ad esso
		getOnStudentGroupCreatedMethod(presenter).invoke(presenter, studentGroup, Set.of(member));
		
		StudentGroup savedStudentGroup = dataService.findStudentGroupById(studentGroup.getId());
		
		// Modifichiamo il nome del gruppo
		savedStudentGroup.setName("StudyApp Group");
		
		// Aggiorniamo il gruppo
		getOnStudentGroupUpdatedMethod(presenter).invoke(presenter, savedStudentGroup, Set.of());
		
		// Recuperiamo il riferimento aggiornato del gruppo
		StudentGroup updatedStudentGroup = dataService.findStudentGroupById(savedStudentGroup.getId());
		
		StudentGroup expectedStudentGroup = new StudentGroup("StudyApp Group", owner, List.of(owner));
		
		// Verifichiamo che il gruppo sia stato aggiornato correttamente
		assertStudentGroupEquals(expectedStudentGroup, updatedStudentGroup);
	}
	
	@Test
	@WithMockUser(username = "mrossi", password = "password", roles = "USER")
	public void nuovo_membro_aggiunto_correttamente() throws InvocationTargetException, 
	IllegalAccessException, NoSuchMethodException {
		
		// Nuovo gruppo senza membri ma con l'owner
		StudentGroup studentGroup = new StudentGroup("SADGroup", owner, new ArrayList<>());
		
		// Nuovo partecipante alla sessione
		Student otherMember = new Student("bcop", "Bianca", "Coppola", 
				"{bcrypt}$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxO/BTk76klW", LocalDate.now(), 
				"Ingegneria Informatica", "2° Anno Magistrale", "bcop@gmail.com", 0, Arrays.asList("ROLE_USER"));
		
		otherMember = dataService.saveStudent(otherMember);
		
		// Il presenter salva il gruppo e richieste relative ad esso
		getOnStudentGroupCreatedMethod(presenter).invoke(presenter, studentGroup, Set.of(member));
		
		// Recuperiamo il riferimento aggiornato del gruppo
		StudentGroup persistentStudentGroup = dataService.findStudentGroupById(studentGroup.getId());
		
		// Aggiorniamo il gruppo fornendo otherMember tra gli utenti selezionati
		getOnStudentGroupUpdatedMethod(presenter).invoke(presenter, persistentStudentGroup, Set.of(otherMember));
		
		persistentStudentGroup = dataService.findStudentGroupById(studentGroup.getId());
		
		// Richiesta di partecipazione al grouppo associato al membro
		StudentGroupRequest expectedStudentGroupRequest = new StudentGroupRequest(studentGroup.getOwner().getUsername() + 
				" invited you to the group " + studentGroup.getName(), otherMember, studentGroup.getId());
		
		// Recuperiamo il riferimento aggiornato di otherMember
		Student persistentOtherMember = dataService.findStudentById(otherMember.getId());
		
		assertStudentGroupRequestEquals(expectedStudentGroupRequest, 
				(StudentGroupRequest) persistentOtherMember.getNotifications().get(0));
	}
	
	@Test
	@WithMockUser(username = "mrossi", password = "password", roles = "USER")
	public void member_rimosso_correttamente() throws InvocationTargetException, 
	IllegalAccessException, NoSuchMethodException {
		
		// Nuovo gruppo senza membri ma con l'owner
		StudentGroup studentGroup = new StudentGroup("SADGroup", owner, new ArrayList<>());
		
		// Nuovo partecipante alla sessione
		Student otherMember = new Student("bcop", "Bianca", "Coppola", 
				"{bcrypt}$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxO/BTk76klW", LocalDate.now(), 
				"Ingegneria Informatica", "2° Anno Magistrale", "bcop@gmail.com", 0, Arrays.asList("ROLE_USER"));
		
		otherMember = dataService.saveStudent(otherMember);
		
		// Il presenter salva il gruppo e richieste relative ad esso
		getOnStudentGroupCreatedMethod(presenter).invoke(presenter, studentGroup, Set.of(member));
		
		// Recuperiamo il riferimento aggiornato del gruppo
		StudentGroup persistentStudentGroup = dataService.findStudentGroupById(studentGroup.getId());
		
		persistentStudentGroup.addMember(member);
		persistentStudentGroup = dataService.saveStudentGroup(persistentStudentGroup);
		
		// Aggiorniamo il gruppo fornendo solo otherMember tra gli utenti selezionati
		getOnStudentGroupUpdatedMethod(presenter).invoke(presenter, persistentStudentGroup, Set.of(otherMember));
		
		// Recuperiamo il riferimento aggiornato di member
		Student persistentMember = dataService.findStudentById(member.getId());

		// Verifichiamo che member non abbia più gruppi
		assertEquals(0, persistentMember.getStudentGroups().size());
	}
	
	@Test
	@WithMockUser(username = "gespo", password = "password", roles = "USER")
	public void member_abbandona_gruppo() throws InvocationTargetException, 
	IllegalAccessException, NoSuchMethodException {
		
		// Nuovo gruppo senza membri ma con l'owner
		StudentGroup studentGroup = new StudentGroup("SADGroup", owner, new ArrayList<>());
		
		// Il presenter salva il gruppo e richieste relative ad esso
		getOnStudentGroupCreatedMethod(presenter).invoke(presenter, studentGroup, Set.of(member));
		
		// Recuperiamo il riferimento aggiornato del gruppo
		StudentGroup persistentStudentGroup = dataService.findStudentGroupById(studentGroup.getId());
		
		persistentStudentGroup.addMember(member);
		persistentStudentGroup = dataService.saveStudentGroup(persistentStudentGroup);
		
		presenter.onGroupClick(persistentStudentGroup);
		presenter.leaveGroup();
		
		persistentStudentGroup = dataService.findStudentGroupById(persistentStudentGroup.getId());
		
		StudentGroup expectedStudentGroup = new StudentGroup("SADGroup", owner, List.of(owner));
		
		assertStudentGroupEquals(expectedStudentGroup, persistentStudentGroup);
	}
	
	@Test
	@WithMockUser(username = "mrossi", password = "password", roles = "USER")
	public void owner_abbandona_gruppo_passando_ownership() throws InvocationTargetException, 
	IllegalAccessException, NoSuchMethodException {
		
		// Nuovo gruppo senza membri ma con l'owner
		StudentGroup studentGroup = new StudentGroup("SADGroup", owner, new ArrayList<>());
		
		// Il presenter salva il gruppo e richieste relative ad esso
		getOnStudentGroupCreatedMethod(presenter).invoke(presenter, studentGroup, Set.of(member));
		
		// Recuperiamo il riferimento aggiornato del gruppo
		StudentGroup persistentStudentGroup = dataService.findStudentGroupById(studentGroup.getId());
		
		persistentStudentGroup.addMember(member);
		persistentStudentGroup = dataService.saveStudentGroup(persistentStudentGroup);
		
		presenter.onGroupClick(persistentStudentGroup);
		presenter.leaveGroup();
		
		persistentStudentGroup = dataService.findStudentGroupById(persistentStudentGroup.getId());
		
		StudentGroup expectedStudentGroup = new StudentGroup("SADGroup", member, List.of(member));
		
		assertStudentGroupEquals(expectedStudentGroup, persistentStudentGroup);
	}
	
	@Test
	@WithMockUser(username = "mrossi", password = "password", roles = "USER")
	public void owner_abbandona_gruppo_eliminandolo() throws InvocationTargetException, 
	IllegalAccessException, NoSuchMethodException {
		
		// Nuovo gruppo senza membri ma con l'owner
		StudentGroup studentGroup = new StudentGroup("SADGroup", owner, new ArrayList<>());
		
		// Il presenter salva il gruppo e richieste relative ad esso
		getOnStudentGroupCreatedMethod(presenter).invoke(presenter, studentGroup, Set.of(member));
		
		// Recuperiamo il riferimento aggiornato del gruppo
		StudentGroup persistentStudentGroup = dataService.findStudentGroupById(studentGroup.getId());
		
		presenter.onGroupClick(persistentStudentGroup);
		presenter.leaveGroup();
		
		persistentStudentGroup = dataService.findStudentGroupById(persistentStudentGroup.getId());
		
		/*
		 * Verifichiamo che il gruppo sia stato eliminato,
		 * in quanto, all'abbandono dell'owner il gruppo
		 * era rimasto senza membri
		 */
		assertEquals(null, persistentStudentGroup);
	}
	
	private void assertStudentGroupEquals(StudentGroup expectedStudentGroup, StudentGroup actualStudentGroup) {
		assertEquals(expectedStudentGroup.getName(), actualStudentGroup.getName());
		assertEquals(expectedStudentGroup.getOwner(), actualStudentGroup.getOwner());

		/*
		 * Il casting serve perchè Hibernate usa PersistentBag (che non
		 * rispetta la collection API sulla funzione di equals) come
		 * implementazione di List. Questo è il caso sia per la lista
		 * di membri del gruppo salvato sia per il gruppo
		 * ottenuto dal database.
		 */
		assertEquals(List.copyOf(expectedStudentGroup.getMembers()), List.copyOf(actualStudentGroup.getMembers()));
	}

	private void assertStudentGroupRequestEquals(StudentGroupRequest expectedRequest, StudentGroupRequest actualRequest) {
		assertEquals(expectedRequest.getMessage(), actualRequest.getMessage());
		assertEquals(expectedRequest.getStudent(), actualRequest.getStudent());
		assertEquals(expectedRequest.getStudentGroupId(), actualRequest.getStudentGroupId());
		assertEquals(expectedRequest.isAccepted(), actualRequest.isAccepted());
	}
	

	private Method getOnStudentGroupCreatedMethod(GroupPresenterImpl presenter) throws NoSuchMethodException {
		Method method = presenter.getClass().getDeclaredMethod("onStudentGroupCreated", StudentGroup.class, Set.class);
		method.setAccessible(true);
		return method;
	}

	private Method getOnStudentGroupRemovedMethod(GroupPresenterImpl presenter) throws NoSuchMethodException {
		Method method = presenter.getClass().getDeclaredMethod("onStudentGroupRemoved", StudentGroup.class);
		method.setAccessible(true);
		return method;
	}
	
	private Method getOnStudentGroupUpdatedMethod(GroupPresenterImpl presenter) throws NoSuchMethodException {
		Method method = presenter.getClass().getDeclaredMethod("onStudentGroupUpdated", StudentGroup.class, Set.class);
		method.setAccessible(true);
		return method;
	}
}
