package it.studyapp.application.entity;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.vaadin.stefan.fullcalendar.Entry;

import jakarta.annotation.Nullable;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Entity
public class CalendarEntryEntity extends AbstractEntity {
	
	@NotNull
	private String originalID;
	
	@NotEmpty
	private String title;
	
	private String description;
	
	@Nullable
	private LocalDateTime startDateTime;
	
	@Nullable
	private LocalDateTime endDateTime;
	
	@NotBlank
	private String color;
	
	@NotNull
	private Boolean allDay;
	
	@NotNull
	private Boolean recurring;
	
	@Nullable
    private LocalDateTime recurringStart;

	@Nullable
    private LocalDateTime recurringEnd;

	@Nullable
    private Set<DayOfWeek> recurringDaysOfWeek;
	
	@NotNull
	private Boolean editable;
	
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "calendarentryentity_student",
    	joinColumns = @JoinColumn(name = "calendarentryentity_id"),
    	inverseJoinColumns = @JoinColumn(name = "student_id"))
	@NotNull
	private List<Student> participants = new ArrayList<>();
	
	public CalendarEntryEntity() {
		this.originalID = "";
		this.title = " ";
		this.description = "";
		this.startDateTime = LocalDateTime.now();
		this.endDateTime = this.startDateTime.plusHours(1);
		this.color = "dodgerblue";
		this.allDay = Boolean.valueOf(false);
		this.recurring = Boolean.valueOf(false);
		this.recurringStart = null;
		this.recurringEnd = null;
		this.recurringDaysOfWeek = null;
		this.editable = Boolean.valueOf(true);
	}
	
	public CalendarEntryEntity(Entry entry) {
		this(entry.getId(), entry.getTitle(), entry.getDescription(), entry.getStartWithOffset(), entry.getEndWithOffset(), entry.getColor(), 
				Boolean.valueOf(entry.isAllDay()), Boolean.valueOf(entry.isRecurring()), 
        		entry.getRecurringStart(), entry.getRecurringEnd(), entry.getRecurringDaysOfWeek(), true);
	}

	public CalendarEntryEntity(@NotNull String originalID, @NotEmpty String title, String description, LocalDateTime startDateTime, LocalDateTime endDateTime,
			@NotBlank String color, @NotNull Boolean allDay, @NotNull Boolean recurring, LocalDateTime recurringStart,
			LocalDateTime recurringEnd, Set<DayOfWeek> recurringDaysOfWeek, @NotNull Boolean editable) {
		this.originalID = originalID;
		this.title = title;
		this.description = description;
		this.startDateTime = startDateTime;
		this.endDateTime = endDateTime;
		this.color = color;
		this.allDay = allDay;
		this.recurring = recurring;
		this.recurringStart = recurringStart;
		this.recurringEnd = recurringEnd;
		this.recurringDaysOfWeek = recurringDaysOfWeek;
		this.editable = editable;
	}
	
	public CalendarEntryEntity(@NotNull String originalID, @NotEmpty String title, String description, LocalDateTime startDateTime, LocalDateTime endDateTime,
			@NotBlank String color, @NotNull Boolean allDay, @NotNull Boolean recurring, LocalDateTime recurringStart,
			LocalDateTime recurringEnd, Set<DayOfWeek> recurringDaysOfWeek, @NotNull Boolean editable, @NotNull List<Student> participants) {
		this.originalID = originalID;
		this.title = title;
		this.description = description;
		this.startDateTime = startDateTime;
		this.endDateTime = endDateTime;
		this.color = color;
		this.allDay = allDay;
		this.recurring = recurring;
		this.recurringStart = recurringStart;
		this.recurringEnd = recurringEnd;
		this.recurringDaysOfWeek = recurringDaysOfWeek;
		this.editable = editable;
		this.participants = participants;
	}
	
	public String getOriginalID() {
		return originalID;
	}
	
	public void setOriginalID(String originalID) {
		this.originalID = originalID;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public LocalDateTime getStartDateTime() {
		return startDateTime;
	}

	public void setStartDateTime(LocalDateTime startDateTime) {
		this.startDateTime = startDateTime;
	}

	public LocalDateTime getEndDateTime() {
		return endDateTime;
	}

	public void setEndDateTime(LocalDateTime endDateTime) {
		this.endDateTime = endDateTime;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public Boolean getAllDay() {
		return allDay;
	}

	public void setAllDay(Boolean allDay) {
		this.allDay = allDay;
	}

	public Boolean getRecurring() {
		return recurring;
	}

	public void setRecurring(Boolean recurring) {
		this.recurring = recurring;
	}
	
	public LocalDateTime getRecurringStart() {
		return recurringStart;
	}


	public void setRecurringStart(LocalDateTime recurringStart) {
		this.recurringStart = recurringStart;
	}


	public LocalDateTime getRecurringEnd() {
		return recurringEnd;
	}


	public void setRecurringEnd(LocalDateTime recurringEnd) {
		this.recurringEnd = recurringEnd;
	}


	public Set<DayOfWeek> getRecurringDaysOfWeek() {
		return recurringDaysOfWeek;
	}


	public void setRecurringDaysOfWeek(Set<DayOfWeek> recurringDaysOfWeek) {
		this.recurringDaysOfWeek = recurringDaysOfWeek;
	}

	public List<Student> getParticipants() {
		return participants;
	}

	public void setParticipants(List<Student> participants) {
		this.participants = participants;
	}
	
	public void addParticipant(Student participant) {
		if(!this.participants.contains(participant))
			this.participants.add(participant);
	}
	
	public void addParticipants(Collection<? extends Student> c) {
		for(Student s : c) {
			if(!this.participants.contains(s))
				this.participants.add(s);
		}
	}
	
	public void removeParticipant(Student participant) {
		this.participants.remove(participant);
	}
	
	public void removeAllParticipants() {
		this.participants.clear();
	}

	public boolean isAllDay() {
		return this.allDay;
	}
	
	public boolean isRecurring() {
		return this.recurring;
	}
	
	public boolean isEditable() {
		return this.editable;
	}
	
	@Override
	public String toString() {
		return String.format("Id: %d, Entry ID: %s, Title: %s, Start: %s, End: %s, Color: %s", 
				this.getId(), this.originalID, this.title, this.startDateTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")), this.endDateTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")), this.color);
	}

	/*
	public static void updateEntry(CalendarEntryEntity calendarEntry, Entry entry) {
        calendarEntry.setTitle(entry.getTitle());
        calendarEntry.setStartDateTime(entry.getStart());
        calendarEntry.setEndDateTime(entry.getEnd());
        calendarEntry.setColor(entry.getColor());
        calendarEntry.setAllDay(entry.isAllDay());
        calendarEntry.setRecurring(entry.isRecurring());
        calendarEntry.setRecurringStart(entry.getRecurringStart());
        calendarEntry.setRecurringEnd(entry.getRecurringEnd());
        calendarEntry.setRecurringDaysOfWeek(entry.getRecurringDaysOfWeek());
	}
	*/

}
