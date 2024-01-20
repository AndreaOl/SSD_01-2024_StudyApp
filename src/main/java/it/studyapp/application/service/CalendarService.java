package it.studyapp.application.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import it.studyapp.application.entity.Reminder;
import it.studyapp.application.entity.Session;
import it.studyapp.application.entity.Student;
import it.studyapp.application.security.SecurityService;

@Service
public class CalendarService {

	private final DataService dataService;
	private final SecurityService securityService;

	private List<Session> weekEvents;
	private List<Session> tomorrowEvents;
	private List<Session> todayEvents;

	public CalendarService(DataService dataService, SecurityService securityService) {
		this.dataService = dataService;
		this.securityService = securityService;
	}

	public void checkEvents() {
		deleteOldReminders();
		
		weekEvents = new ArrayList<Session>();
		tomorrowEvents = new ArrayList<Session>();
		todayEvents = new ArrayList<Session>();
		
		List<Session> sessions = dataService.searchStudent(securityService.getAuthenticatedUser().getUsername())
				.get(0).getSessions();

		weekEvents.addAll(sessions.stream().filter( s ->
		(s.getDate().toLocalDate().isAfter(LocalDate.now()) || s.getDate().toLocalDate().isEqual(LocalDate.now()))
		&& (s.getDate().toLocalDate().isBefore(LocalDate.now().plusDays(7)) || s.getDate().toLocalDate().isEqual(LocalDate.now().plusDays(6)))).toList());

		tomorrowEvents.addAll(sessions.stream().filter( s ->
		s.getDate().toLocalDate().isEqual(LocalDate.now().plusDays(1))).toList());

		todayEvents.addAll(sessions.stream().filter( s ->
		s.getDate().toLocalDate().isEqual(LocalDate.now())).toList());

		sendNotifications();
	}

	private void deleteOldReminders() {
		List<Reminder> reminders = dataService.searchStudent(securityService.getAuthenticatedUser().getUsername())
				.get(0).getNotifications().stream().filter(n -> n instanceof Reminder).map(n -> (Reminder) n).toList();
		
		reminders.forEach(r -> dataService.deleteReminder(r));
	}

	private void sendNotifications() {
		if(weekEvents.isEmpty()) {
			return;
		}
	
		List<Reminder> reminders = new ArrayList<Reminder>();
		Student thisStudent = dataService.searchStudent(securityService.getAuthenticatedUser().getUsername()).get(0);
		
		reminders.add(new Reminder("You have " + weekEvents.size() + " sessions in the next 7 days", thisStudent));
		
		tomorrowEvents.forEach(session ->{
			String sessionTime = session.getDate().format(DateTimeFormatter.ofPattern("HH:mm"));
			String sessionSubject = session.getSubject();
			reminders.add(new Reminder("You have a session tomorrow: " + sessionSubject + " at " + sessionTime, thisStudent));
		});
		
		todayEvents.forEach(session ->{
			String sessionTime = session.getDate().format(DateTimeFormatter.ofPattern("HH:mm"));		
			String sessionSubject = session.getSubject();
			reminders.add(new Reminder("You have a session today: " + sessionSubject + " at " + sessionTime, thisStudent));
		});
		
		reminders.forEach(reminder -> dataService.saveReminder(reminder));
	}
}

