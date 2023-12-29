package it.studyapp.application.ui.calendar;

import org.vaadin.stefan.fullcalendar.DatesRenderedEvent;
import org.vaadin.stefan.fullcalendar.EntryClickedEvent;
import org.vaadin.stefan.fullcalendar.EntryDroppedEvent;
import org.vaadin.stefan.fullcalendar.EntryResizedEvent;
import org.vaadin.stefan.fullcalendar.MoreLinkClickedEvent;
import org.vaadin.stefan.fullcalendar.TimeslotClickedEvent;
import org.vaadin.stefan.fullcalendar.TimeslotsSelectedEvent;

import com.vaadin.flow.component.UI;


public class CalendarLayout extends AbstractCalendarLayout {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CalendarLayout() {
		super();
	}

    @Override
    protected void onEntryResized(EntryResizedEvent event) {
    	
    }

    @Override
    protected void onEntryDropped(EntryDroppedEvent event) {
    	UI.getCurrent().navigate("calendar");
    }

    @Override
    protected void onEntryClick(EntryClickedEvent event) {
        UI.getCurrent().navigate("calendar");
    }

    @Override
    protected void onDatesRendered(DatesRenderedEvent event) {
        super.onDatesRendered(event);
    }

    @Override
    protected void onMoreLinkClicked(MoreLinkClickedEvent event) {
        UI.getCurrent().navigate("calendar");
    }

    @Override
    protected void onTimeslotClicked(TimeslotClickedEvent event) {
    	UI.getCurrent().navigate("calendar");
    }


    @Override
    protected void onTimeslotsSelected(TimeslotsSelectedEvent event) {
    	UI.getCurrent().navigate("calendar");
    }


}