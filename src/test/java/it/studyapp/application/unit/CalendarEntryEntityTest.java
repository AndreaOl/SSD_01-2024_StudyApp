package it.studyapp.application.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import it.studyapp.application.entity.CalendarEntryEntity;
import it.studyapp.application.entity.Student;

public class CalendarEntryEntityTest {

	@Test
	public void addParticipant() {
		// Entry del calendario senza partecipanti
		CalendarEntryEntity testEntry = new CalendarEntryEntity(UUID.randomUUID().toString(), "Ricevimento SAD", "", 
				LocalDateTime.now(), LocalDateTime.now().plusHours(2), "dodgerblue", false, false, null, null, null, true,
				new ArrayList<>());

		// Nuovo partecipante
		Student student = new Student("gespo", "Giuseppe", "Esposito", "password", LocalDate.now(),
				"Ingegneria Informatica", "2° Anno Magistrale", "gespo@gmail.com", 0, Arrays.asList("ROLE_USER"));

		testEntry.addParticipant(student);

		CalendarEntryEntity expectedEntry = new CalendarEntryEntity(UUID.randomUUID().toString(), "Ricevimento SAD", "", 
				LocalDateTime.now(), LocalDateTime.now().plusHours(2), "dodgerblue", false, false, null, null, null, true,
				Arrays.asList(student));

		assertEquals(expectedEntry.getParticipants(), testEntry.getParticipants());
	}

	@Test
	public void removeParticipant() {
		// Entry del calendario senza partecipanti
		CalendarEntryEntity testEntry = new CalendarEntryEntity(UUID.randomUUID().toString(), "Ricevimento SAD", "", 
				LocalDateTime.now(), LocalDateTime.now().plusHours(2), "dodgerblue", false, false, null, null, null, true,
				new ArrayList<>());

		// Nuovo partecipante
		Student student = new Student("gespo", "Giuseppe", "Esposito", "password", LocalDate.now(),
				"Ingegneria Informatica", "2° Anno Magistrale", "gespo@gmail.com", 0, Arrays.asList("ROLE_USER"));

		testEntry.addParticipant(student);
		testEntry.removeParticipant(student);

		CalendarEntryEntity expectedEntry = new CalendarEntryEntity(UUID.randomUUID().toString(), "Ricevimento SAD", "", 
				LocalDateTime.now(), LocalDateTime.now().plusHours(2), "dodgerblue", false, false, null, null, null, true,
				new ArrayList<>());

		assertEquals(expectedEntry.getParticipants(), testEntry.getParticipants());
	}

}
