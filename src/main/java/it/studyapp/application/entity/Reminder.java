package it.studyapp.application.entity;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;

import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
public class Reminder extends NotificationEntity {
	
	public Reminder() {
		super();
	}
	
	public Reminder(@NotBlank String message, @NotNull Student student) {
		super(message, student);
	}
	
	@Override
	public HorizontalLayout create() {
		
		Icon icon = VaadinIcon.CALENDAR_CLOCK.create();
		layout = new HorizontalLayout(icon, new Text(message), createViewButton());
		layout.setAlignItems(Alignment.CENTER);
		layout.getStyle().setPadding("10px");
		
		return layout;
	}
	
	private Button createViewButton() {
	    Button viewBtn = new Button("View", clickEvent -> UI.getCurrent().navigate("calendar"));
	    //viewBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

	    return viewBtn;
	}

}
