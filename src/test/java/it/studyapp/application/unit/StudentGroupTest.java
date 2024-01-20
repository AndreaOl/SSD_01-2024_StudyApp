package it.studyapp.application.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.studyapp.application.entity.Student;
import it.studyapp.application.entity.StudentGroup;

public class StudentGroupTest {
	
	private Student owner;
	
	@BeforeEach
	public void setup() {
		owner = new Student("mrossi", "keycloakId", "Mario", "Rossi", LocalDate.now(),
				"Ingegneria Informatica", "2° Anno Magistrale", "mrossi@gmail.com", 0);
	}
	
	@Test
	public void addMember() {
		// Gruppo con solo l'owner come membro
		StudentGroup testGroup = new StudentGroup("Progetto SAD", owner, new ArrayList<>(Arrays.asList(owner)));
		
		// Nuovo membro
		Student student = new Student("gespo", "keycloakId", "Giuseppe", "Esposito", LocalDate.now(),
				"Ingegneria Informatica", "2° Anno Magistrale", "gespo@gmail.com", 0);
		
		testGroup.addMember(student);
		
		StudentGroup expectedGroup = new StudentGroup("Progetto SAD", owner, Arrays.asList(owner, student));
		
		assertEquals(expectedGroup.getMembers(), testGroup.getMembers());
	}
	
	@Test
	public void removeMember() {
		// Gruppo con solo l'owner come membro
		StudentGroup testGroup = new StudentGroup("Progetto SAD", owner, new ArrayList<>(Arrays.asList(owner)));
		
		// Nuovo membro
		Student student = new Student("gespo", "keycloakId", "Giuseppe", "Esposito", LocalDate.now(),
				"Ingegneria Informatica", "2° Anno Magistrale", "gespo@gmail.com", 0);
		
		testGroup.addMember(student);
		testGroup.removeMember(student);
		
		StudentGroup expectedGroup = new StudentGroup("Progetto SAD", owner, Arrays.asList(owner));
		
		assertEquals(expectedGroup.getMembers(), testGroup.getMembers());
	}

}
