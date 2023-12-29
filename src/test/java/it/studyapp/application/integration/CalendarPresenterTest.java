package it.studyapp.application.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
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
import org.vaadin.stefan.fullcalendar.Entry;

import it.studyapp.application.entity.CalendarEntryEntity;
import it.studyapp.application.entity.Student;
import it.studyapp.application.presenter.calendar.CalendarPresenterImpl;
import it.studyapp.application.service.DataService;
import it.studyapp.application.view.calendar.CalendarView;

@SpringBootTest
@AutoConfigureMockMvc
@TestExecutionListeners(listeners = DependencyInjectionTestExecutionListener.class,
	mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class CalendarPresenterTest {

	static {
		// Prevent Vaadin Development mode to launch browser window
		System.setProperty("vaadin.launch-browser", "false");
	}

	private Student user;

	private CalendarView dummyView;

	@Autowired
	private CalendarPresenterImpl presenter;

	@Autowired
	private DataService dataService;

	@BeforeEach
	public void setup() {

		user = new Student("mrossi", "Mario", "Rossi", "{bcrypt}$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxO/BTk76klW", 
				LocalDate.now(), "Ingegneria Informatica", "2° Anno Magistrale", "mrossi@gmail.com", 0, Arrays.asList("ROLE_USER"));

		user = dataService.saveStudent(user);

		dummyView = new CalendarView() {
			@Override
			public void removeEntries(Collection<Entry> entries) {}
			@Override
			public void removeAllEntries() {}
			@Override
			public void refreshEntry(Entry entry) {}
			@Override
			public void refreshAllEntries() {}
			@Override
			public void addEntry(Entry entry) {}
			@Override
			public void addEntries(Collection<Entry> entries) {}
		};

		presenter.setView(dummyView);
	}

	@Test
	@WithMockUser(username = "mrossi", password = "password", roles = "USER")
	public void evento_creato_correttamente() {

		// Nuovo evento del calendario non ricorrente nè per tutto il giorno
		Entry entry = new Entry();
		entry.setTitle("Ricevimento");
		entry.setDescription("Discussione stato del progetto");
		entry.setStartWithOffset(LocalDateTime.of(2023, 12, 8, 15, 0));
		entry.setEndWithOffset(LocalDateTime.of(2023, 12, 8, 17, 0));
		entry.setColor("dodgerblue");
		entry.setAllDay(false);
		entry.clearRecurringStart();
		entry.clearRecurringEnd();
		entry.setRecurringDaysOfWeek();

		// Creaiamo l'evento persistente a partire da entry
		presenter.onEntriesCreated(List.of(entry));

		Student persistentUser = dataService.findStudentById(user.getId());

		CalendarEntryEntity expectedCalendarEntry = new CalendarEntryEntity(entry.getId(), "Ricevimento",
				"Discussione stato del progetto", LocalDateTime.of(2023, 12, 8, 15, 0),
				LocalDateTime.of(2023, 12, 8, 17, 0), "dodgerblue", false, false, null, null, null, true, List.of(persistentUser));

		CalendarEntryEntity persistentCalendarEntry = dataService.searchCalendarEntry(entry.getId()).get(0);

		assertCalendarEntryEquals(expectedCalendarEntry, persistentCalendarEntry);
	}

	@Test
	@WithMockUser(username = "mrossi", password = "password", roles = "USER")
	public void evento_rimosso_correttamente() {

		// Nuovo evento del calendario non ricorrente nè per tutto il giorno
		Entry entry = new Entry();
		entry.setTitle("Ricevimento");
		entry.setDescription("Discussione stato del progetto");
		entry.setStartWithOffset(LocalDateTime.of(2023, 12, 8, 15, 0));
		entry.setEndWithOffset(LocalDateTime.of(2023, 12, 8, 17, 0));
		entry.setColor("dodgerblue");
		entry.setAllDay(false);
		entry.clearRecurringStart();
		entry.clearRecurringEnd();
		entry.setRecurringDaysOfWeek();

		presenter.onEntriesCreated(List.of(entry));

		// Rimuoviamo l'evento persistente a partire da entry
		presenter.onEntriesRemoved(List.of(entry));

		// Recuperiamo il riferimento aggiornato dell'evento del calendario
		List<CalendarEntryEntity> entryList = dataService.searchCalendarEntry(entry.getId());

		// Verifichiamo che non ci sia l'evento del calendario
		assertEquals(0, entryList.size());
	}

	@Test
	@WithMockUser(username = "mrossi", password = "password", roles = "USER")
	public void evento_aggiornato_correttamente() {

		// Nuovo evento del calendario non ricorrente nè per tutto il giorno
		Entry entry = new Entry();
		entry.setTitle("Ricevimento");
		entry.setDescription("Discussione stato del progetto");
		entry.setStartWithOffset(LocalDateTime.of(2023, 12, 8, 15, 0));
		entry.setEndWithOffset(LocalDateTime.of(2023, 12, 8, 17, 0));
		entry.setColor("dodgerblue");
		entry.setAllDay(false);
		entry.clearRecurringStart();
		entry.clearRecurringEnd();
		entry.setRecurringDaysOfWeek();

		presenter.onEntriesCreated(List.of(entry));

		// Aggiorniamo i campi dell'entry rendendola ricorrente e per tutto il giorno
		entry.setTitle("Riunione");
		entry.setDescription("Pianificazione test");
		entry.clearStart();
		entry.clearEnd();
		entry.setColor("orange");
		entry.setAllDay(true);
		entry.setRecurringStart(LocalDateTime.of(2023, 10, 6, 0, 0));
		entry.setRecurringEnd(LocalDateTime.of(2023, 12, 8, 0, 0));
		entry.setRecurringDaysOfWeek(Set.of(DayOfWeek.MONDAY, DayOfWeek.FRIDAY));

		// Aggiorniamo l'evento persistente a partire da entry
		presenter.onEntryChanged(entry);

		Student persistentUser = dataService.findStudentById(user.getId());

		CalendarEntryEntity expectedCalendarEntry = new CalendarEntryEntity(entry.getId(), "Riunione",
				"Pianificazione test", null, null, "orange", true, true, LocalDateTime.of(2023, 10, 6, 0, 0),
				LocalDateTime.of(2023, 12, 8, 0, 0), Set.of(DayOfWeek.MONDAY, DayOfWeek.FRIDAY), true, List.of(persistentUser));

		CalendarEntryEntity persistentCalendarEntry = dataService.searchCalendarEntry(entry.getId()).get(0);

		assertCalendarEntryEquals(expectedCalendarEntry, persistentCalendarEntry);
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

}
