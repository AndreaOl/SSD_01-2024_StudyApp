package it.studyapp.application.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

import it.studyapp.application.entity.CalendarEntryEntity;
import it.studyapp.application.entity.Session;
import it.studyapp.application.entity.SessionRequest;
import it.studyapp.application.entity.Student;
import it.studyapp.application.presenter.session.SessionPresenterImpl;
import it.studyapp.application.service.DataService;
import it.studyapp.application.view.session.SessionView;

@SpringBootTest
@AutoConfigureMockMvc
@TestExecutionListeners(listeners = DependencyInjectionTestExecutionListener.class,
	mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class SessionPresenterTest {

	static {
		// Prevent Vaadin Development mode to launch browser window
		System.setProperty("vaadin.launch-browser", "false");
	}

	private Student owner;
	private Student participant;
	private SessionView dummyView;

	@Autowired
	private SessionPresenterImpl presenter;

	@Autowired
	private DataService dataService;

	@BeforeEach
	public void setup() {
		owner = new Student("mrossi", "Mario", "Rossi", "{bcrypt}$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxO/BTk76klW", 
				LocalDate.now(), "Ingegneria Informatica", "2° Anno Magistrale", "mrossi@gmail.com", 0, Arrays.asList("ROLE_USER"));

		participant = new Student("gespo", "Giuseppe", "Esposito", "{bcrypt}$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxO/BTk76klW", 
				LocalDate.now(), "Ingegneria Informatica", "2° Anno Magistrale", "gespo@gmail.com", 0, Arrays.asList("ROLE_USER"));

		owner = dataService.saveStudent(owner);
		participant = dataService.saveStudent(participant);

		dummyView = new SessionView() {
			@Override
			public void setSessionGridItems(List<Session> items) {}
			@Override
			public void setSessionGridCount(int count) {}
			@Override
			public void setParticipantsGridItems(List<Student> items) {}
			@Override
			public void setParticipantsGridCount(int count) {}
			@Override
			public void showParticipants() {}
			@Override
			public void hideParticipants() {}
		};

		presenter.setView(dummyView);
	}

	@Test
	@WithMockUser(username = "mrossi", password = "password", roles = "USER")
	public void sessione_creata_correttamente() throws InvocationTargetException, 
	IllegalAccessException, NoSuchMethodException {

		// Nuova sessione senza partecipanti ma con l'owner
		Session session = new Session("SAD", LocalDateTime.of(2023, 12, 20, 10, 0), 
				"Aula studio", owner, new ArrayList<>());

		// Il presenter salva la sessione ed evento del calendario e richieste relativi ad essa
		getOnSessionCreatedMethod(presenter).invoke(presenter, session, Set.of(participant));
		
		// Recuperiamo il riferimento aggiornato della sessione
		Session persistentSession = dataService.findSessionById(session.getId());
		
		// Verifichiamo che la sessione sia stata salvata correttamente
		assertSessionEquals(session, persistentSession);
	}
	
	@Test
	@WithMockUser(username = "mrossi", password = "password", roles = "USER")
	public void evento_calendario_relativo_a_sessione_creato_correttamente() throws InvocationTargetException, 
	IllegalAccessException, NoSuchMethodException {
		
		// Nuova sessione senza partecipanti ma con l'owner
		Session session = new Session("SAD", LocalDateTime.of(2023, 12, 20, 10, 0), 
				"Aula studio", owner, new ArrayList<>());
		
		// Il presenter salva la sessione ed evento del calendario e richieste relativi ad essa
		getOnSessionCreatedMethod(presenter).invoke(presenter, session, Set.of(participant));
		
		// Evento del calendario relativo alla sessione
		CalendarEntryEntity expectedCalendarEntry = new CalendarEntryEntity(session.getEntryId(), 
				"Session - " + session.getSubject(), "", session.getDate(), session.getDate().plusHours(1), 
				"dodgerblue", false, false, null, null, null, false, List.of(owner));
		
		CalendarEntryEntity persistentCalendarEntry = dataService.searchCalendarEntry(session.getEntryId()).get(0);
		
		assertCalendarEntryEquals(expectedCalendarEntry, persistentCalendarEntry);
	}
	
	@Test
	@WithMockUser(username = "mrossi", password = "password", roles = "USER")
	public void owner_ha_la_sessione_creata() throws InvocationTargetException, 
	IllegalAccessException, NoSuchMethodException {
		
		// Nuova sessione senza partecipanti ma con l'owner
		Session session = new Session("SAD", LocalDateTime.of(2023, 12, 20, 10, 0), 
				"Aula studio", owner, new ArrayList<>());
		
		// Il presenter salva la sessione ed evento del calendario e richieste relativi ad essa
		getOnSessionCreatedMethod(presenter).invoke(presenter, session, Set.of(participant));
		
		// Recuperiamo il riferimento aggiornato dell'owner dal database
		Student persistentOwner = dataService.findStudentById(owner.getId());

		// Verifichiamo che l'owner abbia la sessione corretta
		assertSessionEquals(session, persistentOwner.getSessions().get(0));
	}
	
	@Test
	@WithMockUser(username = "mrossi", password = "password", roles = "USER")
	public void owner_ha_evento_calendario_relativo_a_sessione_creata() throws InvocationTargetException, 
	IllegalAccessException, NoSuchMethodException {
		
		// Nuova sessione senza partecipanti ma con l'owner
		Session session = new Session("SAD", LocalDateTime.of(2023, 12, 20, 10, 0), 
				"Aula studio", owner, new ArrayList<>());
		
		// Il presenter salva la sessione ed evento del calendario e richieste relativi ad essa
		getOnSessionCreatedMethod(presenter).invoke(presenter, session, Set.of(participant));
		
		// Evento del calendario relativo alla sessione
		CalendarEntryEntity expectedCalendarEntry = new CalendarEntryEntity(session.getEntryId(), 
				"Session - " + session.getSubject(), "", session.getDate(), session.getDate().plusHours(1), 
				"dodgerblue", false, false, null, null, null, false, List.of(owner));
		
		// Recuperiamo il riferimento aggiornato dell'owner dal database
		Student persistentOwner = dataService.findStudentById(owner.getId());

		// Verifichiamo che l'owner abbia l'evento del calendario corretto
		assertCalendarEntryEquals(expectedCalendarEntry, persistentOwner.getCalendarEntries().get(0));
	}
	
	@Test
	@WithMockUser(username = "mrossi", password = "password", roles = "USER")
	public void richiesta_sessione_creata_correttamente() throws InvocationTargetException, 
	IllegalAccessException, NoSuchMethodException {
		
		// Nuova sessione senza partecipanti ma con l'owner
		Session session = new Session("SAD", LocalDateTime.of(2023, 12, 20, 10, 0), 
				"Aula studio", owner, new ArrayList<>());

		// Il presenter salva la sessione ed evento del calendario e richieste relativi ad essa
		getOnSessionCreatedMethod(presenter).invoke(presenter, session, Set.of(participant));
		
		// Richiesta di partecipazione alla sessione associata al participant
		SessionRequest expectedSessionRequest = new SessionRequest(session.getOwner().getUsername() + 
				" invited you to the study session " + session.getSubject(), participant, session.getId());
		
		Student persistentParticipant = dataService.findStudentById(participant.getId());
		
		assertSessionRequestEquals(expectedSessionRequest, (SessionRequest) persistentParticipant.getNotifications().get(0));
	}
	
	@Test
	@WithMockUser(username = "mrossi", password = "password", roles = "USER")
	public void participant_non_ha_sessione_alla_creazione() throws InvocationTargetException, 
	IllegalAccessException, NoSuchMethodException {
		
		// Nuova sessione senza partecipanti ma con l'owner
		Session session = new Session("SAD", LocalDateTime.of(2023, 12, 20, 10, 0), 
				"Aula studio", owner, new ArrayList<>());

		// Il presenter salva la sessione ed evento del calendario e richieste relativi ad essa
		getOnSessionCreatedMethod(presenter).invoke(presenter, session, Set.of(participant));
		
		Student persistentParticipant = dataService.findStudentById(participant.getId());
		
		// Verifichiamo che il participant non abbia ancora la sessione
		assertEquals(0, persistentParticipant.getSessions().size());
	}
	
	@Test
	@WithMockUser(username = "mrossi", password = "password", roles = "USER")
	public void participant_non_ha_evento_calendario_alla_creazione_della_sessione() throws InvocationTargetException, 
	IllegalAccessException, NoSuchMethodException {
		
		// Nuova sessione senza partecipanti ma con l'owner
		Session session = new Session("SAD", LocalDateTime.of(2023, 12, 20, 10, 0), 
				"Aula studio", owner, new ArrayList<>());

		// Il presenter salva la sessione ed evento del calendario e richieste relativi ad essa
		getOnSessionCreatedMethod(presenter).invoke(presenter, session, Set.of(participant));
		
		Student persistentParticipant = dataService.findStudentById(participant.getId());
		
		// Verifichiamo che il participant non abbia ancora l'evento del calendario
		assertEquals(0, persistentParticipant.getCalendarEntries().size());
	}
	
	@Test
	@WithMockUser(username = "mrossi", password = "password", roles = "USER")
	public void sessione_rimossa_correttamente() throws InvocationTargetException, 
	IllegalAccessException, NoSuchMethodException {
		
		// Nuova sessione senza partecipanti ma con l'owner
		Session session = new Session("SAD", LocalDateTime.of(2023, 12, 20, 10, 0), 
				"Aula studio", owner, new ArrayList<>());

		// Salviamo la sessione e le entità relative ad essa
		getOnSessionCreatedMethod(presenter).invoke(presenter, session, Set.of(participant));

		// Recuperiamo il riferimento aggiornato della sessione
		Session persistentSession = dataService.findSessionById(session.getId());

		// Il presenter elimina sessione ed evento del calendario e richieste relativi ad essa
		getOnSessionRemovedMethod(presenter).invoke(presenter, persistentSession);

		// Recuperiamo il riferimento aggiornato della sessione
		persistentSession = dataService.findSessionById(session.getId());

		// Verifichiamo che la sessione non esista più
		assertEquals(null, persistentSession);
	}
	
	@Test
	@WithMockUser(username = "mrossi", password = "password", roles = "USER")
	public void evento_calendario_relativo_a_sessione_rimosso_correttamente() throws InvocationTargetException, 
	IllegalAccessException, NoSuchMethodException {
		
		// Nuova sessione senza partecipanti ma con l'owner
		Session session = new Session("SAD", LocalDateTime.of(2023, 12, 20, 10, 0), 
				"Aula studio", owner, new ArrayList<>());

		// Salviamo la sessione e le entità relative ad essa
		getOnSessionCreatedMethod(presenter).invoke(presenter, session, Set.of(participant));

		// Recuperiamo il riferimento aggiornato della sessione
		Session persistentSession = dataService.findSessionById(session.getId());

		// Il presenter elimina sessione ed evento del calendario e richieste relativi ad essa
		getOnSessionRemovedMethod(presenter).invoke(presenter, persistentSession);
		
		// Recuperiamo il riferimento aggiornato dell'evento del calendario
		List<CalendarEntryEntity> entryList = dataService.searchCalendarEntry(session.getEntryId());

		// Verifichiamo che non ci siano più eventi del calendario relativi alla sessione
		assertEquals(0, entryList.size());
	}
	
	@Test
	@WithMockUser(username = "mrossi", password = "password", roles = "USER")
	public void owner_non_ha_sessione_dopo_rimozione() throws InvocationTargetException, 
	IllegalAccessException, NoSuchMethodException {
		
		// Nuova sessione senza partecipanti ma con l'owner
		Session session = new Session("SAD", LocalDateTime.of(2023, 12, 20, 10, 0), 
				"Aula studio", owner, new ArrayList<>());

		// Salviamo la sessione e le entità relative ad essa
		getOnSessionCreatedMethod(presenter).invoke(presenter, session, Set.of(participant));

		// Recuperiamo il riferimento aggiornato della sessione
		Session persistentSession = dataService.findSessionById(session.getId());

		// Il presenter elimina sessione ed evento del calendario e richieste relativi ad essa
		getOnSessionRemovedMethod(presenter).invoke(presenter, persistentSession);
		
		// Recuperiamo il riferimento aggiornato dell'owner dal database
		Student persistentOwner = dataService.findStudentById(owner.getId());

		// Verifichiamo che l'owner non abbia più la sessione
		assertEquals(0, persistentOwner.getSessions().size());
	}
	
	@Test
	@WithMockUser(username = "mrossi", password = "password", roles = "USER")
	public void owner_non_ha_evento_calendario_dopo_rimozione_sessione() throws InvocationTargetException, 
	IllegalAccessException, NoSuchMethodException {
		
		// Nuova sessione senza partecipanti ma con l'owner
		Session session = new Session("SAD", LocalDateTime.of(2023, 12, 20, 10, 0), 
				"Aula studio", owner, new ArrayList<>());

		// Salviamo la sessione e le entità relative ad essa
		getOnSessionCreatedMethod(presenter).invoke(presenter, session, Set.of(participant));

		// Recuperiamo il riferimento aggiornato della sessione
		Session persistentSession = dataService.findSessionById(session.getId());

		// Il presenter elimina sessione ed evento del calendario e richieste relativi ad essa
		getOnSessionRemovedMethod(presenter).invoke(presenter, persistentSession);
		
		// Recuperiamo il riferimento aggiornato dell'owner dal database
		Student persistentOwner = dataService.findStudentById(owner.getId());
		
		// Verifichiamo che l'owner non abbia più l'evento del calendario
		assertEquals(0, persistentOwner.getCalendarEntries().size());
	}
	
	@Test
	@WithMockUser(username = "mrossi", password = "password", roles = "USER")
	public void participant_non_ha_richiesta_dopo_rimozione_sessione() throws InvocationTargetException, 
	IllegalAccessException, NoSuchMethodException {
		
		// Nuova sessione senza partecipanti ma con l'owner
		Session session = new Session("SAD", LocalDateTime.of(2023, 12, 20, 10, 0), 
				"Aula studio", owner, new ArrayList<>());

		// Salviamo la sessione e le entità relative ad essa
		getOnSessionCreatedMethod(presenter).invoke(presenter, session, Set.of(participant));

		// Recuperiamo il riferimento aggiornato della sessione
		Session persistentSession = dataService.findSessionById(session.getId());

		// Il presenter elimina sessione ed evento del calendario e richieste relativi ad essa
		getOnSessionRemovedMethod(presenter).invoke(presenter, persistentSession);
		
		// Recuperiamo il riferimento aggiornato del participant dal database
		Student persistentParticipant = dataService.findStudentById(participant.getId());

		// Verifichiamo che il participant non abbia più richieste
		assertEquals(0, persistentParticipant.getNotifications().size());
	}
	
	@Test
	@WithMockUser(username = "mrossi", password = "password", roles = "USER")
	public void campi_sessione_aggiornati_correttamente() throws InvocationTargetException, 
	IllegalAccessException, NoSuchMethodException {
		
		// Nuova sessione senza partecipanti ma con l'owner
		Session session = new Session("SAD", LocalDateTime.of(2023, 12, 20, 10, 0), 
				"Aula studio", owner, new ArrayList<>());
		
		// Salviamo la sessione e le entità relative ad essa
		getOnSessionCreatedMethod(presenter).invoke(presenter, session, Set.of(participant));
		
		// Recuperiamo il riferimento aggiornato della sessione
		Session savedSession = dataService.findSessionById(session.getId());
		
		// Modifichiamo data e luogo della sessione
		savedSession.setSubject("Test progetto");
		savedSession.setDate(LocalDateTime.of(2024, 1, 2, 15, 30));
		savedSession.setLocation("Discord");

		// Aggiorniamo la sessione
		getOnSessionUpdatedMethod(presenter).invoke(presenter, savedSession, Set.of());
		
		// Recuperiamo il riferimento aggiornato della sessione
		Session updatedSession = dataService.findSessionById(savedSession.getId());

		Session expectedSession = new Session("Test progetto", LocalDateTime.of(2024, 1, 2, 15, 30), 
				"Discord", owner, List.of(owner));

		// Verifichiamo che la sessione sia stata aggiornata correttamente
		assertSessionEquals(expectedSession, updatedSession);
	}
	
	@Test
	@WithMockUser(username = "mrossi", password = "password", roles = "USER")
	public void nuovo_partecipante_aggiunto_correttamente() throws InvocationTargetException, 
	IllegalAccessException, NoSuchMethodException {
		
		// Nuova sessione senza partecipanti ma con l'owner
		Session session = new Session("SAD", LocalDateTime.of(2023, 12, 20, 10, 0), 
				"Aula studio", owner, new ArrayList<>());

		// Nuovo partecipante alla sessione
		Student otherParticipant = new Student("bcop", "Bianca", "Coppola", 
				"{bcrypt}$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxO/BTk76klW", LocalDate.now(), 
				"Ingegneria Informatica", "2° Anno Magistrale", "bcop@gmail.com", 0, Arrays.asList("ROLE_USER"));

		otherParticipant = dataService.saveStudent(otherParticipant);

		// Salviamo la sessione e le entità relative ad essa
		getOnSessionCreatedMethod(presenter).invoke(presenter, session, Set.of(participant));

		// Recuperiamo il riferimento aggiornato della sessione
		Session persistentSession = dataService.findSessionById(session.getId());
		
		// Aggiorniamo la sessione fornendo otherParticipant tra gli utenti selezionati
		getOnSessionUpdatedMethod(presenter).invoke(presenter, persistentSession, Set.of(otherParticipant));
		
		// Recuperiamo il riferimento aggiornato della sessione
		persistentSession = dataService.findSessionById(session.getId());
		
		// Richiesta di partecipazione alla sessione associata ad otherParticipant
		SessionRequest expectedSessionRequest = new SessionRequest(persistentSession.getOwner().getUsername() + 
				" invited you to the study session " + persistentSession.getSubject(),
				otherParticipant, persistentSession.getId());

		// Recuperiamo il riferimento aggiornato di otherParticipant
		Student persistentOtherParticipant = dataService.findStudentById(otherParticipant.getId());
		
		// Verifichiamo che otherParticipant abbia la richiesta corretta (non accettata)
		assertSessionRequestEquals(expectedSessionRequest, (SessionRequest) persistentOtherParticipant.getNotifications().get(0));
	}
	
	@Test
	@WithMockUser(username = "mrossi", password = "password", roles = "USER")
	public void participant_rimosso_correttamente() throws InvocationTargetException, 
	IllegalAccessException, NoSuchMethodException {
		
		// Nuova sessione senza partecipanti ma con l'owner
		Session session = new Session("SAD", LocalDateTime.of(2023, 12, 20, 10, 0), 
				"Aula studio", owner, new ArrayList<>());

		// Nuovo partecipante alla sessione
		Student otherParticipant = new Student("bcop", "Bianca", "Coppola", 
				"{bcrypt}$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxO/BTk76klW", LocalDate.now(), 
				"Ingegneria Informatica", "2° Anno Magistrale", "bcop@gmail.com", 0, Arrays.asList("ROLE_USER"));

		otherParticipant = dataService.saveStudent(otherParticipant);

		// Salviamo la sessione e le entità relative ad essa
		getOnSessionCreatedMethod(presenter).invoke(presenter, session, Set.of(participant));

		// Recuperiamo il riferimento aggiornato della sessione
		Session persistentSession = dataService.findSessionById(session.getId());
		
		// Aggiungiamo il participant alla sessione
		persistentSession.addParticipant(participant);
		persistentSession = dataService.saveSession(persistentSession);

		// Aggiungiamo participant all'evento del calendario
		CalendarEntryEntity calendarEntry = dataService.searchCalendarEntry(persistentSession.getEntryId()).get(0);
		calendarEntry.addParticipant(participant);
		dataService.saveCalendarEntry(calendarEntry);
		
		// Aggiorniamo la sessione fornendo solo otherParticipant tra gli utenti selezionati
		getOnSessionUpdatedMethod(presenter).invoke(presenter, persistentSession, Set.of(otherParticipant));
		
		// Recuperiamo il riferimento aggiornato di participant
		Student persistentParticipant = dataService.findStudentById(participant.getId());

		// Verifichiamo che participant non abbia più sessioni 
		assertEquals(0, persistentParticipant.getSessions().size());

		// Verifichiamo che participant non abbia più eventi del calendario
		assertEquals(0, persistentParticipant.getCalendarEntries().size());
	}
	
	@Test
	@WithMockUser(username = "gespo", password = "password", roles = "USER")
	public void participant_abbandona_sessione() throws InvocationTargetException, 
	IllegalAccessException, NoSuchMethodException {
		
		// Nuova sessione senza partecipanti ma con l'owner
		Session session = new Session("SAD", LocalDateTime.of(2023, 12, 20, 10, 0), 
				"Aula studio", owner, new ArrayList<>());
		
		// Salviamo la sessione e le entità relative ad essa
		getOnSessionCreatedMethod(presenter).invoke(presenter, session, Set.of(participant));

		// Recuperiamo il riferimento aggiornato della sessione
		Session persistentSession = dataService.findSessionById(session.getId());
		
		// Aggiungiamo il participant alla sessione
		persistentSession.addParticipant(participant);
		persistentSession = dataService.saveSession(persistentSession);

		// Aggiungiamo participant all'evento del calendario
		CalendarEntryEntity calendarEntry = dataService.searchCalendarEntry(persistentSession.getEntryId()).get(0);
		calendarEntry.addParticipant(participant);
		dataService.saveCalendarEntry(calendarEntry);
		
		presenter.onSessionClick(persistentSession);
		presenter.leaveSession();
		
		persistentSession = dataService.findSessionById(persistentSession.getId());
		
		Session expectedSession = new Session("SAD", LocalDateTime.of(2023, 12, 20, 10, 0), 
				"Aula studio", owner, List.of(owner));
		
		assertSessionEquals(expectedSession, persistentSession);
	}
	
	@Test
	@WithMockUser(username = "mrossi", password = "password", roles = "USER")
	public void owner_abbandona_sessione_passando_ownership() throws InvocationTargetException, 
	IllegalAccessException, NoSuchMethodException {
		
		// Nuova sessione senza partecipanti ma con l'owner
		Session session = new Session("SAD", LocalDateTime.of(2023, 12, 20, 10, 0), 
				"Aula studio", owner, new ArrayList<>());
		
		// Salviamo la sessione e le entità relative ad essa
		getOnSessionCreatedMethod(presenter).invoke(presenter, session, Set.of(participant));

		// Recuperiamo il riferimento aggiornato della sessione
		Session persistentSession = dataService.findSessionById(session.getId());
		
		// Aggiungiamo il participant alla sessione
		persistentSession.addParticipant(participant);
		persistentSession = dataService.saveSession(persistentSession);

		// Aggiungiamo participant all'evento del calendario
		CalendarEntryEntity calendarEntry = dataService.searchCalendarEntry(persistentSession.getEntryId()).get(0);
		calendarEntry.addParticipant(participant);
		dataService.saveCalendarEntry(calendarEntry);
		
		presenter.onSessionClick(persistentSession);
		presenter.leaveSession();
		
		persistentSession = dataService.findSessionById(persistentSession.getId());
		
		Session expectedSession = new Session("SAD", LocalDateTime.of(2023, 12, 20, 10, 0), 
				"Aula studio", participant, List.of(participant));
		
		assertSessionEquals(expectedSession, persistentSession);
	}
	
	@Test
	@WithMockUser(username = "mrossi", password = "password", roles = "USER")
	public void owner_abbandona_sessione_eliminandola() throws InvocationTargetException, 
	IllegalAccessException, NoSuchMethodException {
		
		// Nuova sessione senza partecipanti ma con l'owner
		Session session = new Session("SAD", LocalDateTime.of(2023, 12, 20, 10, 0), 
				"Aula studio", owner, new ArrayList<>());
		
		// Salviamo la sessione e le entità relative ad essa
		getOnSessionCreatedMethod(presenter).invoke(presenter, session, Set.of(participant));

		// Recuperiamo il riferimento aggiornato della sessione
		Session persistentSession = dataService.findSessionById(session.getId());
		
		presenter.onSessionClick(persistentSession);
		presenter.leaveSession();
		
		persistentSession = dataService.findSessionById(persistentSession.getId());
		
		/*
		 * Verifichiamo che la sessione sia stata eliminata,
		 * in quanto, all'abbandono dell'owner la sessione
		 * era rimasta senza partecipanti
		 */
		assertEquals(null, persistentSession);
	}

	private void assertSessionEquals(Session expectedSession, Session actualSession) {
		assertEquals(expectedSession.getSubject(), actualSession.getSubject());
		assertEquals(expectedSession.getDate(), actualSession.getDate());
		assertEquals(expectedSession.getLocation(), actualSession.getLocation());
		assertEquals(expectedSession.getOwner(), actualSession.getOwner());

		/*
		 * Il casting serve perchè Hibernate usa PersistentBag (che non
		 * rispetta la collection API sulla funzione di equals) come
		 * implementazione di List. Questo è il caso sia per la lista
		 * di partecipanti della sessione salvata sia per la sessione
		 * ottenuta dal database.
		 */
		assertEquals(List.copyOf(expectedSession.getParticipants()), List.copyOf(actualSession.getParticipants()));
	}

	private void assertCalendarEntryEquals(CalendarEntryEntity expectedEntry, CalendarEntryEntity actualEntry) {
		assertEquals(expectedEntry.getOriginalID(), actualEntry.getOriginalID());
		assertEquals(expectedEntry.getTitle(), actualEntry.getTitle());
		assertEquals(expectedEntry.getDescription(), actualEntry.getDescription());
		assertEquals(expectedEntry.getStartDateTime(), actualEntry.getStartDateTime());
		assertEquals(expectedEntry.getColor(), actualEntry.getColor());
		assertEquals(expectedEntry.getAllDay(), actualEntry.getAllDay());
		assertEquals(expectedEntry.getRecurring(), actualEntry.getRecurring());
		assertEquals(expectedEntry.getRecurringStart(), actualEntry.getRecurringStart());
		assertEquals(expectedEntry.getRecurringEnd(), actualEntry.getRecurringEnd());
		assertEquals(expectedEntry.getRecurringDaysOfWeek(), actualEntry.getRecurringDaysOfWeek());
		assertEquals(expectedEntry.getParticipants(), actualEntry.getParticipants());
	}

	private void assertSessionRequestEquals(SessionRequest expectedRequest, SessionRequest actualRequest) {
		assertEquals(expectedRequest.getMessage(), actualRequest.getMessage());
		assertEquals(expectedRequest.getStudent(), actualRequest.getStudent());
		assertEquals(expectedRequest.getSessionId(), actualRequest.getSessionId());
		assertEquals(expectedRequest.isAccepted(), actualRequest.isAccepted());
	}

	private Method getOnSessionCreatedMethod(SessionPresenterImpl presenter) throws NoSuchMethodException {
		Method method = presenter.getClass().getDeclaredMethod("onSessionCreated", Session.class, Set.class);
		method.setAccessible(true);
		return method;
	}

	private Method getOnSessionRemovedMethod(SessionPresenterImpl presenter) throws NoSuchMethodException {
		Method method = presenter.getClass().getDeclaredMethod("onSessionRemoved", Session.class);
		method.setAccessible(true);
		return method;
	}

	private Method getOnSessionUpdatedMethod(SessionPresenterImpl presenter) throws NoSuchMethodException {
		Method method = presenter.getClass().getDeclaredMethod("onSessionUpdated", Session.class, Set.class);
		method.setAccessible(true);
		return method;
	}

}