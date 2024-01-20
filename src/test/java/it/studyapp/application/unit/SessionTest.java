package it.studyapp.application.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.studyapp.application.entity.Session;
import it.studyapp.application.entity.Student;

public class SessionTest {

	private Student owner;

	@BeforeEach
	public void setup() {
		owner = new Student("mrossi", "keycloakId", "Mario", "Rossi", LocalDate.now(),
				"Ingegneria Informatica", "2° Anno Magistrale", "mrossi@gmail.com", 0);
	}

	@Test
	public void addParticipant() {
		// Sessione con solo l'owner come partecipante
		Session testSession = new Session("SAD", LocalDateTime.now(), "Aula studio", owner, new ArrayList<>(Arrays.asList(owner)));

		// Nuovo partecipante
		Student student = new Student("gespo", "keycloakId", "Giuseppe", "Esposito", LocalDate.now(),
				"Ingegneria Informatica", "2° Anno Magistrale", "gespo@gmail.com", 0);

		testSession.addParticipant(student);

		Session expectedSession = new Session("SAD", LocalDateTime.now(), "Aula studio", owner, Arrays.asList(owner, student));

		assertEquals(expectedSession.getParticipants(), testSession.getParticipants());
	}

	@Test
	public void removeParticipant() {
		// Sessione con solo l'owner come partecipante
		Session testSession = new Session("SAD", LocalDateTime.now(), "Aula studio", owner, new ArrayList<>(Arrays.asList(owner)));
		
		// Nuovo partecipante
		Student student = new Student("gespo", "keycloakId", "Giuseppe", "Esposito", LocalDate.now(),
				"Ingegneria Informatica", "2° Anno Magistrale", "gespo@gmail.com", 0);

		testSession.addParticipant(student);
		testSession.removeParticipant(student);

		Session expectedSession = new Session("SAD", LocalDateTime.now(), "Aula studio", owner, Arrays.asList(owner));

		assertEquals(expectedSession.getParticipants(), testSession.getParticipants());
	}

}
