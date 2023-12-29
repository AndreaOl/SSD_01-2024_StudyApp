package it.studyapp.application.ui.calendar;

import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import elemental.json.Json;
import elemental.json.JsonObject;

import org.vaadin.stefan.fullcalendar.BrowserTimezoneObtainedEvent;
import org.vaadin.stefan.fullcalendar.BusinessHours;
import org.vaadin.stefan.fullcalendar.DatesRenderedEvent;
import org.vaadin.stefan.fullcalendar.DayNumberClickedEvent;
import org.vaadin.stefan.fullcalendar.Entry;
import org.vaadin.stefan.fullcalendar.EntryClickedEvent;
import org.vaadin.stefan.fullcalendar.EntryDroppedEvent;
import org.vaadin.stefan.fullcalendar.EntryResizedEvent;
import org.vaadin.stefan.fullcalendar.FullCalendar;
import org.vaadin.stefan.fullcalendar.FullCalendarBuilder;
import org.vaadin.stefan.fullcalendar.MoreLinkClickedEvent;
import org.vaadin.stefan.fullcalendar.TimeslotClickedEvent;
import org.vaadin.stefan.fullcalendar.TimeslotsSelectedEvent;
import org.vaadin.stefan.fullcalendar.ViewSkeletonRenderedEvent;
import org.vaadin.stefan.fullcalendar.WeekNumberClickedEvent;
import org.vaadin.stefan.fullcalendar.dataprovider.EntryProvider;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Locale;

/**
 * A basic class for simple calendar views, e.g. for demo or testing purposes. Takes care of
 * creating a toolbar, a description element and embedding the created calendar into the view.
 * Also registers a dates rendered listener to update the toolbar.
 */
@Uses(FullCalendar.class)
public abstract class AbstractCalendarLayout extends VerticalLayout {
	
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private final FullCalendar calendar;
    protected CalendarToolbar toolbar;

    public AbstractCalendarLayout() {
        calendar = createCalendar(createDefaultInitialOptions());

        calendar.addEntryClickedListener(this::onEntryClick);
        calendar.addEntryDroppedListener(this::onEntryDropped);
        calendar.addEntryResizedListener(this::onEntryResized);
        calendar.addDayNumberClickedListener(this::onDayNumberClicked);
        calendar.addBrowserTimezoneObtainedListener(this::onBrowserTimezoneObtained);
        calendar.addMoreLinkClickedListener(this::onMoreLinkClicked);
        calendar.addTimeslotClickedListener(this::onTimeslotClicked);
        calendar.addTimeslotsSelectedListener(this::onTimeslotsSelected);
        calendar.addViewSkeletonRenderedListener(this::onViewSkeletonRendered);
        calendar.addDatesRenderedListener(this::onDatesRendered);
        calendar.addWeekNumberClickedListener(this::onWeekNumberClicked);
        
        toolbar = new CalendarToolbar(calendar);
        
        VerticalLayout container = new VerticalLayout(toolbar, calendar);

        container.setDefaultHorizontalComponentAlignment(Alignment.CENTER);

        calendar.setWidth("95%");
        
        container.setHeight("95%");
        container.setWidth("95%");
        
        add(container);
        
        setHorizontalComponentAlignment(Alignment.CENTER, container);
        setSizeFull();

        postConstruct(calendar);
    }

    protected boolean isToolbarDateChangeable() {
        return true;
    }

    protected boolean isToolbarViewChangeable() {
        return true;
    }

    protected boolean isToolbarSettingsAvailable() {
        return true;
    }

    protected void postConstruct(FullCalendar calendar) {
        // NOOP
    }

    /**
     * Creates the plain full calendar instance with all initial options. The given default initial options are created by
     * {@link #createDefaultInitialOptions()} beforehand.
     * <p></p>
     * The calender is automatically embedded afterwards and connected with the toolbar (if one is created, which
     * is the default). Also all event listeners will be initialized with a default callback method.
     *
     * @param defaultInitialOptions default initial options
     * @return calendar instance
     */
    protected FullCalendar createCalendar(JsonObject defaultInitialOptions) {
        FullCalendar calendar = FullCalendarBuilder.create()
                .withAutoBrowserTimezone()
                .withInitialOptions(defaultInitialOptions)
                .withEntryLimit(3)
                .build();

        calendar.setLocale(Locale.US);
        calendar.setFirstDay(DayOfWeek.MONDAY);
        calendar.setNowIndicatorShown(true);
        calendar.setNumberClickable(false);
        calendar.setTimeslotsSelectable(true);

        // initally change the view and go to a specific date - attention: this will not fire listeners as the client side is not initialized yet
//            calendar.changeView(CalendarViewImpl.TIME_GRID_WEEK);
//            calendar.gotoDate(LocalDate.of(2023, Month.JUNE, 1));

        calendar.setSlotMinTime(LocalTime.of(7, 0));
        calendar.setSlotMaxTime(LocalTime.of(17, 0));

        calendar.setBusinessHours(
                new BusinessHours(LocalTime.of(9, 0), LocalTime.of(17, 0), BusinessHours.DEFAULT_BUSINESS_WEEK),
                new BusinessHours(LocalTime.of(12, 0), LocalTime.of(15, 0), DayOfWeek.SATURDAY),
                new BusinessHours(LocalTime.of(12, 0), LocalTime.of(13, 0), DayOfWeek.SUNDAY)
        );
        
        return calendar;
    }

    /**
     * Creates a default set of initial options.
     *
     * @return initial options
     */
    protected JsonObject createDefaultInitialOptions() {
        JsonObject initialOptions = Json.createObject();
        JsonObject eventTimeFormat = Json.createObject();
        eventTimeFormat.put("hour", "2-digit");
        eventTimeFormat.put("minute", "2-digit");
        eventTimeFormat.put("meridiem", false);
        eventTimeFormat.put("hour12", false);
        initialOptions.put("eventTimeFormat", eventTimeFormat);
        return initialOptions;
    }

    /**
     * Called by the calendar's entry click listener. Noop by default.
     * @see FullCalendar#addEntryClickedListener(ComponentEventListener)
     * @param event event
     */
    protected abstract void onEntryClick(EntryClickedEvent event);

    /**
     * Called by the calendar's entry drop listener (i. e. an entry has been dragged around / moved by the user).
     * Applies the changes to the entry and calls {@link #onEntryChanged(Entry)} by default.
     * @see FullCalendar#addEntryDroppedListener(ComponentEventListener)
     * @param event event
     */
    protected abstract void onEntryDropped(EntryDroppedEvent event);

    /**
     * Called by the calendar's entry resize listener.
     * Applies the changes to the entry and calls {@link #onEntryChanged(Entry)} by default.
     * @see FullCalendar#addEntryResizedListener(ComponentEventListener)
     * @param event event
     */
    protected abstract void onEntryResized(EntryResizedEvent event);

    /**
     * Called by the calendar's week number click listener. Noop by default.
     * @see FullCalendar#addWeekNumberClickedListener(ComponentEventListener)
     * @param event event
     */
    protected void onWeekNumberClicked(WeekNumberClickedEvent event) {

    }

    /**
     * Called by the calendar's dates rendered listener. Noop by default.
     * Please note, that there is a separate dates rendered listener taking
     * care of updating the toolbar.
     * @see FullCalendar#addDatesRenderedListener(ComponentEventListener)
     * @param event event
     */
    protected void onDatesRendered(DatesRenderedEvent event) {
    	toolbar.updateDate(event.getIntervalStart());
    }

    /**
     * Called by the calendar's view skeleton rendered listener. Noop by default.
     * @see FullCalendar#addViewSkeletonRenderedListener(ComponentEventListener)
     * @param event event
     */
    protected void onViewSkeletonRendered(ViewSkeletonRenderedEvent event) {

    }
    
    /**
     * Called by the calendar's timeslot selected listener. Noop by default.
     * @see FullCalendar#addTimeslotsSelectedListener(ComponentEventListener)
     * @param event event
     */
    protected abstract void onTimeslotsSelected(TimeslotsSelectedEvent event);

    /**
     * Called by the calendar's timeslot clicked listener. Noop by default.
     * @see FullCalendar#addTimeslotClickedListener(ComponentEventListener)
     * @param event event
     */
    protected void onTimeslotClicked(TimeslotClickedEvent event) {

    }

    /**
     * Called by the calendar's "more" link clicked listener. Noop by default.
     * @see FullCalendar#addMoreLinkClickedListener(ComponentEventListener)
     * @param event event
     */
    protected void onMoreLinkClicked(MoreLinkClickedEvent event) {
    	
    }

    /**
     * Called by the calendar's browser timezone obtained listener. Noop by default.
     * Please note, that the full calendar builder registers also a listener, when the
     * {@link FullCalendarBuilder#withAutoBrowserTimezone()} option is used.
     * @see FullCalendar#addBrowserTimezoneObtainedListener(ComponentEventListener)
     * @param event event
     */
    protected void onBrowserTimezoneObtained(BrowserTimezoneObtainedEvent event) {

    }

    /**
     * Called by the calendar's day number click listener. Noop by default.
     * @see FullCalendar#addDayNumberClickedListener(ComponentEventListener)
     * @param event event
     */
    protected void onDayNumberClicked(DayNumberClickedEvent event) {

    }


    /**
     * Returns the entry provider set to the calendar. Will be available after {@link #createCalendar(JsonObject)}
     * has been called.
     * @return entry provider or null
     */
    protected EntryProvider<Entry> getEntryProvider() {
        return getCalendar().getEntryProvider();
    }

	public FullCalendar getCalendar() {
		return calendar;
	}

}