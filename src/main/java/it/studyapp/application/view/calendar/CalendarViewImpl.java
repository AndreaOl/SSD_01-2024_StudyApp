package it.studyapp.application.view.calendar;

import java.util.Collection;

import org.vaadin.stefan.fullcalendar.Entry;
import org.vaadin.stefan.fullcalendar.EntryClickedEvent;
import org.vaadin.stefan.fullcalendar.EntryDroppedEvent;
import org.vaadin.stefan.fullcalendar.EntryResizedEvent;
import org.vaadin.stefan.fullcalendar.TimeslotsSelectedEvent;
import org.vaadin.stefan.fullcalendar.dataprovider.InMemoryEntryProvider;

import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import it.studyapp.application.presenter.calendar.CalendarPresenter;
import it.studyapp.application.ui.calendar.AbstractCalendarLayout;
import it.studyapp.application.view.layout.MainLayoutImpl;
import jakarta.annotation.security.PermitAll;


@PageTitle("Calendar")
@Route(value = "calendar", layout = MainLayoutImpl.class)
@PermitAll
public class CalendarViewImpl extends AbstractCalendarLayout implements CalendarView {
    	
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private CalendarPresenter presenter;

	public CalendarViewImpl(CalendarPresenter presenter) {
		super();
		
		this.presenter = presenter;
		this.presenter.setView(this);
		this.presenter.updateCalendar();
	}


	@Override
    protected void onEntryResized(EntryResizedEvent event) {
		presenter.onEntryResized(event);
    }

    @Override
    protected void onEntryDropped(EntryDroppedEvent event) {
    	presenter.onEntryDropped(event);
    }

    @Override
    protected void onEntryClick(EntryClickedEvent event) {
        presenter.onEntryClick(event.getEntry());
    }


    @Override
    protected void onTimeslotsSelected(TimeslotsSelectedEvent event) {
        presenter.onTimeslotsSelected(event);
    }


	@Override
	public void removeEntries(Collection<Entry> entries) {
		if(getCalendar().isInMemoryEntryProvider()) {
            InMemoryEntryProvider<Entry> provider = getCalendar().getEntryProvider();
            provider.removeEntries(entries);
        }
	}


	@Override
	public void removeAllEntries() {
		if(getCalendar().isInMemoryEntryProvider()) {
            InMemoryEntryProvider<Entry> provider = getCalendar().getEntryProvider();
            provider.removeAllEntries();
        }		
	}


	@Override
	public void refreshEntry(Entry entry) {
		if(getCalendar().isInMemoryEntryProvider()) {
            InMemoryEntryProvider<Entry> provider = getCalendar().getEntryProvider();
            provider.refreshItem(entry);
        }		
	}


	@Override
	public void refreshAllEntries() {
		if(getCalendar().isInMemoryEntryProvider()) {
            InMemoryEntryProvider<Entry> provider = getCalendar().getEntryProvider();
            provider.refreshAll();
        }		
	}


	@Override
	public void addEntry(Entry entry) {
		if(getCalendar().isInMemoryEntryProvider()) {
			InMemoryEntryProvider<Entry> provider = getCalendar().getEntryProvider();
            provider.addEntry(entry);
        }		
	}


	@Override
	public void addEntries(Collection<Entry> entries) {
		if (getCalendar().isInMemoryEntryProvider()) {
            InMemoryEntryProvider<Entry> entryProvider = getCalendar().getEntryProvider();
            entryProvider.addEntries(entries);
        }		
	}
	    	            

}