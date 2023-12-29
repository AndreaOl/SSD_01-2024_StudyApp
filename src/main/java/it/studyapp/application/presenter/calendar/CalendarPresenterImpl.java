package it.studyapp.application.presenter.calendar;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.vaadin.stefan.fullcalendar.DisplayMode;
import org.vaadin.stefan.fullcalendar.Entry;
import org.vaadin.stefan.fullcalendar.EntryDroppedEvent;
import org.vaadin.stefan.fullcalendar.EntryResizedEvent;
import org.vaadin.stefan.fullcalendar.TimeslotsSelectedEvent;

import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;

import it.studyapp.application.entity.CalendarEntryEntity;
import it.studyapp.application.entity.Student;
import it.studyapp.application.event.SessionRemovedEvent;
import it.studyapp.application.event.SessionRequestAcceptedEvent;
import it.studyapp.application.event.SessionUpdatedEvent;
import it.studyapp.application.security.SecurityService;
import it.studyapp.application.service.DataService;
import it.studyapp.application.ui.dialog.calendar.EntryDialog;
import it.studyapp.application.util.calendar.EntryManager;
import it.studyapp.application.view.calendar.CalendarView;

@Component
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CalendarPresenterImpl implements CalendarPresenter {
	
	private CalendarView view;
	
	@Autowired
	private DataService dataService;
	
	@Autowired
	private SecurityService securityService;
	
	public CalendarPresenterImpl() {
		UI currentUI = UI.getCurrent();
		
		if(currentUI != null) {
			ComponentUtil.addListener(UI.getCurrent(), SessionRequestAcceptedEvent.class, e -> updateCalendar());
			ComponentUtil.addListener(UI.getCurrent(), SessionUpdatedEvent.class, e -> updateCalendar());
			ComponentUtil.addListener(UI.getCurrent(), SessionRemovedEvent.class, e -> updateCalendar());
		}
	}
	
	@Override
	public void setView(CalendarView view) {
		this.view = view;
	}

	@Override
	public void updateCalendar() {
		view.removeAllEntries();
		view.refreshAllEntries();
		
	    Student thisStudent = dataService.searchStudent(securityService.getAuthenticatedUser().getUsername()).get(0);
	    List<CalendarEntryEntity> databaseEntries = thisStudent.getCalendarEntries();
	    databaseEntries.forEach(databaseEntry -> {
	    	Entry calendarEntry;
	    	
	    	if(databaseEntry.isRecurring()) {
	    		calendarEntry = EntryManager.createRecurringEvent(databaseEntry);
	    	} else {
	    		if(databaseEntry.isAllDay()) {
	    			calendarEntry = EntryManager.createDayEntry(databaseEntry);
	    		} else {
	    			calendarEntry = EntryManager.createTimedEntry(databaseEntry);
	    		}
	    	}
	    	
	    	view.addEntry(calendarEntry);
	    });
	    
	    view.refreshAllEntries();
	}

	@Override
	public void onEntryClick(Entry entry) {
		if(entry.isEditable() && entry.getDisplayMode() != DisplayMode.BACKGROUND && entry.getDisplayMode() != DisplayMode.INVERSE_BACKGROUND) {
            EntryDialog dialog = new EntryDialog(entry, false);
            dialog.setSaveConsumer(this::onEntryChanged);
            dialog.setDeleteConsumer(e -> onEntriesRemoved(Collections.singletonList(e)));
            dialog.open();
        }		
	}

	@Override
	public void onEntryDropped(EntryDroppedEvent event) {
		if(!event.getEntry().isEditable()) {
    		view.refreshEntry(event.getEntry());
    		return;
    	}
		
		event.applyChangesOnEntry();
        onEntryChanged(event.getEntry());		
	}

	@Override
	public void onEntryResized(EntryResizedEvent event) {
    	if(!event.getEntry().isEditable()) {
    		view.refreshEntry(event.getEntry());
    		return;
    	}
    	
		event.applyChangesOnEntry();
        onEntryChanged(event.getEntry());		
	}

	@Override
	public void onEntriesCreated(Collection<Entry> entries) {
        view.addEntries(entries);
        view.refreshAllEntries();
        
        entries.forEach(entry -> {
            CalendarEntryEntity calendarEntry = new CalendarEntryEntity(entry);
            Student thisStudent = dataService.searchStudent(securityService.getAuthenticatedUser().getUsername()).get(0);
            calendarEntry.addParticipant(thisStudent);
            dataService.saveCalendarEntry(calendarEntry);
        });		
	}

	@Override
	public void onEntriesRemoved(Collection<Entry> entries) {
        view.removeEntries(entries);
        view.refreshAllEntries();
        
        entries.forEach(entry -> {
            List<CalendarEntryEntity> databaseEntries = dataService.searchCalendarEntry(entry.getId());
            if(databaseEntries != null && !databaseEntries.isEmpty()) {
            	CalendarEntryEntity databaseEntry = databaseEntries.get(0);
            	dataService.deleteCalendarEntry(databaseEntry);
            }
        });		
	}

	@Override
	public void onEntryChanged(Entry entry) {
        view.refreshEntry(entry);
        
        CalendarEntryEntity databaseEntry = dataService.searchCalendarEntry(entry.getId()).get(0);
        
        databaseEntry.setTitle(entry.getTitle());
        databaseEntry.setDescription(entry.getDescription());
        databaseEntry.setStartDateTime(entry.getStartWithOffset());
        databaseEntry.setEndDateTime(entry.getEndWithOffset());
        databaseEntry.setColor(entry.getColor());
        databaseEntry.setAllDay(entry.isAllDay());
        databaseEntry.setRecurring(entry.isRecurring());
        databaseEntry.setRecurringStart(entry.getRecurringStart());
        databaseEntry.setRecurringEnd(entry.getRecurringEnd());
        databaseEntry.setRecurringDaysOfWeek(entry.getRecurringDaysOfWeek());
        
        dataService.saveCalendarEntry(databaseEntry);	
	}
	
	@Override
	public void onTimeslotsSelected(TimeslotsSelectedEvent event) {
		Entry entry = new Entry();

        entry.setStart(event.getStart());
        entry.setEnd(event.getEnd());
        entry.setAllDay(event.isAllDay());

        entry.setColor("orange");
        entry.setCalendar(event.getSource());

        EntryDialog dialog = new EntryDialog(entry, true);
        dialog.setSaveConsumer(e -> onEntriesCreated(Collections.singletonList(e)));
        dialog.setDeleteConsumer(e -> onEntriesRemoved(Collections.singletonList(e)));
        dialog.open();
	}

}
