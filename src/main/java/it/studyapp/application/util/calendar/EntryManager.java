package it.studyapp.application.util.calendar;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import org.vaadin.stefan.fullcalendar.*;

import it.studyapp.application.entity.CalendarEntryEntity;

public class EntryManager {
	
	public static Entry createRecurringEvent(CalendarEntryEntity databaseEntry) {
		return createRecurringEvent(databaseEntry.getOriginalID(), databaseEntry.getTitle(), databaseEntry.getDescription(),
					databaseEntry.getColor(), databaseEntry.getAllDay(), databaseEntry.getRecurringDaysOfWeek(), 
					databaseEntry.getRecurringStart(), databaseEntry.getRecurringEnd());
	}
	
	public static Entry createRecurringEvent(String id, String title, String description, String color, Boolean allDay, 
			Set<DayOfWeek> recurringDaysOfWeek, LocalDateTime recurringStart, LocalDateTime recurringEnd) {
        
        Entry recurring = new Entry(id);
        recurring.setTitle(title);
        recurring.setDescription(description);
        recurring.setColor(color);
        recurring.setAllDay(allDay.booleanValue());
        
        if(allDay) {
        	recurring.clearStart();
        	recurring.clearEnd();
        }

        recurring.setRecurringDaysOfWeek(recurringDaysOfWeek);
        recurring.setRecurringStart(recurringStart);
        recurring.setRecurringEnd(recurringEnd);
        recurring.setEditable(true);
        
        return recurring;
    }
	
	
	public static Entry createDayEntry(CalendarEntryEntity databaseEntry) {
		return createDayEntry(databaseEntry.getOriginalID(), databaseEntry.getTitle(), databaseEntry.getDescription(),
					databaseEntry.getStartDateTime().toLocalDate(), databaseEntry.getEndDateTime().toLocalDate(), 
					databaseEntry.getColor(), databaseEntry.isEditable());
	}
	
	public static Entry createDayEntry(String id, String title, String description, LocalDate start, LocalDate end, String color, boolean editable) {
        Entry entry = new Entry(id);
        setValues(entry, title, description, start.atStartOfDay(), end.atStartOfDay(), color, true);
        entry.setEditable(editable);
        
        return entry;
    }
	
	
	public static Entry createTimedEntry(CalendarEntryEntity databaseEntry) {
		return createTimedEntry(databaseEntry.getOriginalID(), databaseEntry.getTitle(), databaseEntry.getDescription(),
					databaseEntry.getStartDateTime(), databaseEntry.getEndDateTime(), databaseEntry.getColor());
	}
	
	public static Entry createTimedEntry(String id, String title, String description, LocalDateTime start, LocalDateTime end, String color) {
        Entry entry = new Entry(id);
        setValues(entry, title, description, start, end, color, false);
        entry.setEditable(true);
        
        return entry;
    }
	
	
	public static void setValues(Entry entry, String title, String description, LocalDateTime start, LocalDateTime end, String color, boolean allDay) {
        entry.setTitle(title);
        entry.setDescription(description);
        entry.setStart(Timezone.getSystem().removeTimezoneOffset(start));
        entry.setEnd(Timezone.getSystem().removeTimezoneOffset(end));
        entry.setAllDay(allDay);
        entry.setColor(color);
    }

}
