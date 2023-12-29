package it.studyapp.application.util.data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Component;

import it.studyapp.application.entity.CalendarEntryEntity;
import it.studyapp.application.entity.Session;
import it.studyapp.application.entity.Student;
import it.studyapp.application.entity.StudentGroup;
import it.studyapp.application.security.SecurityConfig;
import it.studyapp.application.service.DataService;

@Component
public class DataGenerator {
	
	private DataService dataService;

	private SecurityConfig securityConfig;

	public DataGenerator(DataService service, SecurityConfig securityConfig) {
		this.dataService = service;
		this.securityConfig = securityConfig;
	}
	
	public void generateData() {

		//pass forzanapoli98
		Student u1 = new Student("andreaol", "Andrea", "Olandese","{bcrypt}$2a$10$b6QUOTGDtZ/vR.qSSYSCH.P8H1wswVhDzwbZ8IhFx95riHexJhAtm",
				LocalDate.of(1999, 1, 23), "Ingegneria Informatica","2° Anno Magistrale","andreaol@hotmail.it", 0, Arrays.asList("ROLE_USER"));
		
		if(securityConfig.getManager().userExists(u1.getUsername()))
			return;
		
		//pass fratone98
		Student u2 = new Student("adricop98", "Adriano", "Coppola","{bcrypt}$2a$10$VFkUYBiQfXFRqV7WePC4Ce5MPjMmnqSDihCTWH6MBAJmQfHhomVbO",
				LocalDate.of(1998, 12, 9), "Ingegneria Informatica","2° Anno Magistrale","adricop98@hotmail.it", 1, Arrays.asList("ROLE_USER"));
		
		//pass technoking98
		Student u3 = new Student("saviog", "Savio", "Guerrisi","{bcrypt}$2a$10$WU9UiQeETitBv4fP0uwZK.ogK2EMc5qKVJFPm06tT30vtn3wPCyO2",
				LocalDate.of(1998, 6, 18),"Ingegneria Informatica","2° Anno Magistrale","saviog@hotmail.it", 2, Arrays.asList("ROLE_USER"));

		List<Student> uList = Arrays.asList(u1, u2, u3);
		uList.forEach(u -> dataService.saveStudent(u));
		
		StudentGroup g1 = new StudentGroup("Progetto SAD", u1, uList);
		dataService.saveStudentGroup(g1);
		
		Session s1 = new Session("SAD", LocalDateTime.of(2023, 11, 20, 10, 0), "Discord", u1, uList);
		Session s2 = new Session("SAD", LocalDateTime.of(2023, 12, 15, 16, 30), "Discord", u1, List.of(u1, u2));
		dataService.saveSession(s1);
		dataService.saveSession(s2);
		
		CalendarEntryEntity s1CalendarEntry = new CalendarEntryEntity(s1.getEntryId(), "Session - " + s1.getSubject(), 
				"", s1.getDate(), s1.getDate().plusHours(1), "dodgerblue", false, false, null, null, null, false, uList);

		dataService.saveCalendarEntry(s1CalendarEntry);
		
		CalendarEntryEntity s2CalendarEntry = new CalendarEntryEntity(s2.getEntryId(), "Session - " + s2.getSubject(), 
				"", s2.getDate(), s2.getDate().plusHours(1), "dodgerblue", false, false, null, null, null, false, List.of(u1, u2));

		dataService.saveCalendarEntry(s2CalendarEntry);
	}

}
