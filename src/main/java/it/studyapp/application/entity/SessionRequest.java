package it.studyapp.application.entity;

import org.vaadin.lineawesome.LineAwesomeIcon;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;

import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
public class SessionRequest extends NotificationEntity {
	
	@NotNull
	private Long sessionId;
	
	@NotNull
	private Boolean accepted;
	
	public SessionRequest() {
		super();
		this.sessionId = 0L;
		this.accepted = Boolean.valueOf(false);
	}

	public SessionRequest(@NotBlank String message, @NotNull Student student, @NotNull Long sessionId) {
		super(message, student);
		this.sessionId = sessionId;
		this.accepted = Boolean.valueOf(false);
	}
	
	public HorizontalLayout create(Runnable onAcceptRunnable) {
		
		Component icon = LineAwesomeIcon.BUSINESS_TIME_SOLID.create();
		layout = new HorizontalLayout(icon, new Text(message), createAcceptButton(onAcceptRunnable));
		layout.setAlignItems(Alignment.CENTER);
		layout.getStyle().setPadding("10px");
		
		return layout;
	}
	
	private Button createAcceptButton(Runnable onAcceptRunnable) {
	    Button acceptBtn = new Button("Accept");
	    
	    if(!accepted)
	    	acceptBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
	    else {
	    	acceptBtn.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
	    	acceptBtn.setText("Accepted");
	    }
	    
	    acceptBtn.addClickListener(clickEvent -> {
	    	onAcceptRunnable.run();
	    	acceptBtn.setText("Accepted");
	    	acceptBtn.removeThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
	    	acceptBtn.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
	    });

	    return acceptBtn;
	}

	public Long getSessionId() {
		return sessionId;
	}

	public void setSessionId(Long sessionId) {
		this.sessionId = sessionId;
	}
	
	public Boolean isAccepted() {
		return accepted;
	}

	public void setAccepted(Boolean accepted) {
		this.accepted = accepted;
	}

	@Override
	public String toString() {
		return String.format("Notification Id: %d, Message: %s, Accepted: %s, Session ID: %d, Student: %s\n", this.getId(), this.message, this.accepted, this.sessionId, this.student.getUsername());
	}
}
