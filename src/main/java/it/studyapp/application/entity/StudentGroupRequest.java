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
public class StudentGroupRequest extends NotificationEntity {
	
	@NotNull
	private Long studentGroupId;
	
	@NotNull
	private Boolean accepted;
	
	public StudentGroupRequest() {
		super();
		this.studentGroupId = 0L;
		this.accepted = Boolean.valueOf(false);
	}

	public StudentGroupRequest(@NotBlank String message, @NotNull Student student, @NotNull Long studentGroupId) {
		super(message, student);
		this.studentGroupId = studentGroupId;
		this.accepted = Boolean.valueOf(false);
	}
	
	public HorizontalLayout create(Runnable onAcceptRunnable) {
		
		Component icon =  LineAwesomeIcon.USER_FRIENDS_SOLID.create();
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

	public Long getStudentGroupId() {
		return studentGroupId;
	}

	public void setStudentGroupId(Long studentGroupId) {
		this.studentGroupId = studentGroupId;
	}
	
	public Boolean isAccepted() {
		return accepted;
	}

	public void setAccepted(Boolean accepted) {
		this.accepted = accepted;
	}

	@Override
	public String toString() {
		return String.format("Notification Id: %d, Message: %s, Accepted: %s, Student Group ID: %d, Student: %s\n", this.getId(), this.message, this.accepted, this.studentGroupId, this.student.getUsername());
	}
}
