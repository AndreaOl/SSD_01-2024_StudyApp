package it.studyapp.application.ui.calendar;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.vaadin.stefan.fullcalendar.FullCalendar;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;

public class CalendarToolbar extends MenuBar {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Button buttonDatePicker;
	
    public CalendarToolbar(FullCalendar calendar) {
    	
    	addThemeVariants(MenuBarVariant.LUMO_SMALL);
    	    	
        addItem(VaadinIcon.ANGLE_LEFT.create(), e -> calendar.previous()).setId("period-previous-button");

        DatePicker gotoDate = new DatePicker();
        gotoDate.addValueChangeListener(event1 -> calendar.gotoDate(event1.getValue()));
        gotoDate.getElement().getStyle().set("visibility", "hidden");
        gotoDate.getElement().getStyle().set("position", "absolute");
        gotoDate.setWidth("0px");
        gotoDate.setHeight("0px");
        gotoDate.setWeekNumbersVisible(true);
        
        buttonDatePicker = new Button(VaadinIcon.CALENDAR.create());
        buttonDatePicker.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.US)));
        buttonDatePicker.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        buttonDatePicker.getElement().appendChild(gotoDate.getElement());
        buttonDatePicker.addClickListener(event -> gotoDate.open());
        buttonDatePicker.setWidthFull();
        
        addItem(buttonDatePicker);
        addItem(VaadinIcon.ANGLE_RIGHT.create(), e -> calendar.next());
        addItem("Today", e -> calendar.today());
       
    }
    
    public void updateDate(LocalDate date) {
    	buttonDatePicker.setText(date.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.US)));
    }

}