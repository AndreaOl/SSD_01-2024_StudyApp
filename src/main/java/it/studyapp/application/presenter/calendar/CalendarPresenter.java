package it.studyapp.application.presenter.calendar;

import java.util.Collection;

import org.vaadin.stefan.fullcalendar.Entry;
import org.vaadin.stefan.fullcalendar.EntryDroppedEvent;
import org.vaadin.stefan.fullcalendar.EntryResizedEvent;
import org.vaadin.stefan.fullcalendar.TimeslotsSelectedEvent;

import it.studyapp.application.view.calendar.CalendarView;

public interface CalendarPresenter {

	public void setView(CalendarView view);
	public void updateCalendar();
	public void onEntryClick(Entry entry);
	public void onEntryDropped(EntryDroppedEvent event);
	public void onEntryResized(EntryResizedEvent event);
	public void onEntriesCreated(Collection<Entry> entries);
	public void onEntriesRemoved(Collection<Entry> entries);
	public void onEntryChanged(Entry entry);
	public void onTimeslotsSelected(TimeslotsSelectedEvent event);

}