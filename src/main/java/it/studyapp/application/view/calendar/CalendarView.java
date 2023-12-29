package it.studyapp.application.view.calendar;

import java.util.Collection;

import org.vaadin.stefan.fullcalendar.Entry;

public interface CalendarView {
	
	public void removeEntries(Collection<Entry> entries);
	public void removeAllEntries();
	public void refreshEntry(Entry entry);
	public void refreshAllEntries();
	public void addEntry(Entry entry);
	public void addEntries(Collection<Entry> entries);

}
