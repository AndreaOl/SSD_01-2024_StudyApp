package it.studyapp.application.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import it.studyapp.application.entity.CalendarEntryEntity;
import it.studyapp.application.entity.Reminder;
import it.studyapp.application.entity.Student;
import it.studyapp.application.security.SecurityService;

@Service
public class CalendarService {

	private final DataService dataService;
	private final SecurityService securityService;

	private List<CalendarEntryEntity> weekEvents;
	private List<CalendarEntryEntity> tomorrowEvents;
	private List<CalendarEntryEntity> todayEvents;

	public CalendarService(DataService dataService, SecurityService securityService) {
		this.dataService = dataService;
		this.securityService = securityService;
	}

	public void checkEvents() {
		weekEvents = new ArrayList<CalendarEntryEntity>();
		tomorrowEvents = new ArrayList<CalendarEntryEntity>();
		todayEvents = new ArrayList<CalendarEntryEntity>();
		
		List<CalendarEntryEntity> calendarEntries= dataService.searchStudent(securityService.getAuthenticatedUser().getUsername())
				.get(0).getCalendarEntries();

		List<CalendarEntryEntity> nonRecurring = calendarEntries.stream().filter(e -> 
		!e.isRecurring()).toList();

		List<CalendarEntryEntity> recurring = calendarEntries.stream().filter(e -> 
		e.isRecurring()).toList();

		weekEvents.addAll(nonRecurring.stream().filter( e ->
		(e.getStartDateTime().toLocalDate().isAfter(LocalDate.now())||e.getStartDateTime().toLocalDate().isEqual(LocalDate.now()))
		&& (e.getStartDateTime().toLocalDate().isBefore(LocalDate.now().plusDays(7)) || e.getStartDateTime().toLocalDate().isEqual(LocalDate.now().plusDays(6)))).toList());

		tomorrowEvents.addAll(nonRecurring.stream().filter( e ->
		e.getStartDateTime().toLocalDate().isEqual(LocalDate.now().plusDays(1))).toList());

		todayEvents.addAll(nonRecurring.stream().filter( e ->
		e.getStartDateTime().toLocalDate().isEqual(LocalDate.now())).toList());


		unpack(recurring);

		sendNotifications();

	}

	private void unpack(List<CalendarEntryEntity> entryList) {
		entryList.forEach( e -> {
			e.setRecurringEnd(e.getRecurringEnd().minusDays(1));
			
			if(e.getRecurringStart().toLocalDate().isAfter(LocalDate.now().plusDays(6))
					|| e.getRecurringEnd().toLocalDate().isBefore(LocalDate.now())){
				return;
			}
			
			if(e.getRecurringStart().toLocalDate().isBefore(LocalDate.now().plusDays(1))
					&& e.getRecurringEnd().toLocalDate().isAfter(LocalDate.now().plusDays(5))) {
				unpackInternal(e);
				return;
			}
			
			unpackIntersect(e);
		});
	}
	
	private void unpackInternal(CalendarEntryEntity entry) {
		entry.getRecurringDaysOfWeek().forEach(day ->{
			int dis = day.compareTo(LocalDate.now().getDayOfWeek());
			if(dis < 0)
				dis = dis + 7;
			
			CalendarEntryEntity tempEntry = new CalendarEntryEntity(entry.getOriginalID(), entry.getTitle(), entry.getDescription(),
					entry.getRecurringStart().withDayOfYear(LocalDateTime.now().plusDays(dis).getDayOfYear()), 
					entry.getRecurringEnd().withDayOfYear(LocalDateTime.now().plusDays(dis+1).getDayOfYear()), entry.getColor(), entry.isAllDay(), 
					false, null, null, null, entry.isEditable());
			
			weekEvents.add(tempEntry);
			
			if(tempEntry.getStartDateTime().toLocalDate().isEqual(LocalDate.now().plusDays(1))) {
				tomorrowEvents.add(tempEntry);
			}
			
			if(tempEntry.getStartDateTime().toLocalDate().isEqual(LocalDate.now())) {
				todayEvents.add(tempEntry);
			}		
		});
	}


	private void unpackIntersect(CalendarEntryEntity entry) {

		Set<DayOfWeek> newset = new HashSet<DayOfWeek>();

		//intersezione a destra
		if(entry.getRecurringStart().toLocalDate().isAfter(LocalDate.now()) &&
				entry.getRecurringEnd().toLocalDate().isAfter(LocalDate.now().plusDays(6))) {
			
			LocalDate currentDay = entry.getRecurringStart().toLocalDate();
			LocalDate endDay = LocalDate.now().plusDays(7);
			while(currentDay.isBefore(endDay)) {
				newset.add(currentDay.getDayOfWeek());
				currentDay = currentDay.plusDays(1);
			}
		}
		
		//intersezione interna
		if(entry.getRecurringStart().toLocalDate().isAfter(LocalDate.now().minusDays(1)) &&
				entry.getRecurringEnd().toLocalDate().isBefore(LocalDate.now().plusDays(7))) {
			
			LocalDate currentDay = entry.getRecurringStart().toLocalDate();
			LocalDate endDay = entry.getRecurringEnd().toLocalDate().plusDays(1);
			while(currentDay.isBefore(endDay)) {
				newset.add(currentDay.getDayOfWeek());
				currentDay = currentDay.plusDays(1);
			}
		}
		
		//intersezione sinistra
		if(entry.getRecurringStart().toLocalDate().isBefore(LocalDate.now()) &&
				entry.getRecurringEnd().toLocalDate().isBefore(LocalDate.now().plusDays(6))) {
			
			LocalDate currentDay = LocalDate.now();
			LocalDate endDay = entry.getRecurringEnd().toLocalDate().plusDays(1);
			while(currentDay.isBefore(endDay)) {
				newset.add(currentDay.getDayOfWeek());
				currentDay = currentDay.plusDays(1);
			}
		}


		entry.getRecurringDaysOfWeek().forEach(day ->{
			if(!newset.contains(day))
				return;
			
			int dis = day.compareTo(LocalDate.now().getDayOfWeek());
			if(dis < 0)
				dis = dis + 7;
			
			CalendarEntryEntity tempEntry = new CalendarEntryEntity(entry.getOriginalID(), entry.getTitle(), entry.getDescription(),
					entry.getRecurringStart().withDayOfYear(LocalDateTime.now().plusDays(dis).getDayOfYear()), 
					entry.getRecurringEnd().withDayOfYear(LocalDateTime.now().plusDays(dis+1).getDayOfYear()), entry.getColor(), entry.isAllDay(),
					false, null, null, null, entry.isEditable());
			
			weekEvents.add(tempEntry);
			
			if(tempEntry.getStartDateTime().toLocalDate().isEqual(LocalDate.now().plusDays(1))) {
				tomorrowEvents.add(tempEntry);
			}
			
			if(tempEntry.getStartDateTime().toLocalDate().isEqual(LocalDate.now())) {
				todayEvents.add(tempEntry);
			}
		});

	}

	private void sendNotifications() {
		if(weekEvents.isEmpty()) {
			return;
		}
	
		List<Reminder> reminders = new ArrayList<Reminder>();
		Student thisStudent = dataService.searchStudent(securityService.getAuthenticatedUser().getUsername()).get(0);
		
		reminders.add(new Reminder("You have " + weekEvents.size() + " events in the next 7 days", thisStudent));
		
		tomorrowEvents.forEach(event ->{
			String eventTime = event.getStartDateTime().format(DateTimeFormatter.ofPattern("HH:mm"));
			
			if(event.isEditable()) {
				reminders.add(new Reminder("You have an event tomorrow: " + event.getTitle() + (event.isAllDay() ? "" : (" at " + eventTime)), thisStudent));
			} else {
				String sessionSubject = event.getTitle().substring(10);
				reminders.add(new Reminder("You have a session tomorrow: " + sessionSubject + " at " + eventTime, thisStudent));				
			}
		});
		
		todayEvents.forEach(event ->{
			String eventTime = event.getStartDateTime().format(DateTimeFormatter.ofPattern("HH:mm"));
			
			if(event.isEditable()) {
				reminders.add(new Reminder("You have an event today: " + event.getTitle() + (event.isAllDay() ? "" : (" at " + eventTime)), thisStudent));
			} else {
				String sessionSubject = event.getTitle().substring(10);
				reminders.add(new Reminder("You have a session today: " + sessionSubject + " at " + eventTime, thisStudent));				
			}
		});
		
		reminders.forEach(reminder -> dataService.saveReminder(reminder));
	}
}

