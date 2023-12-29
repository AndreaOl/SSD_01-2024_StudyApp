package it.studyapp.application.entity;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
public class NotificationEntity extends AbstractEntity {
	
	@NotBlank
	protected String message;
	
	@ManyToOne
    @JoinColumn(name = "student_id")
	@NotNull
    protected Student student;
	
	@Transient
	protected HorizontalLayout layout;
	
	
	public NotificationEntity() {
		this.message = "";
		this.student = null;
	}

	public NotificationEntity(@NotBlank String message, @NotNull Student student) {
		this.message = message;
		this.student = student;
	}
	
	public HorizontalLayout create() {
		
		Icon icon = VaadinIcon.INFO_CIRCLE_O.create();
		layout = new HorizontalLayout(icon, new Text(message));
		layout.setAlignItems(Alignment.CENTER);
		layout.getStyle().setPadding("10px");
		
		return layout;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	public Student getStudent() {
		return student;
	}

	public void setStudent(Student student) {
		this.student = student;
	}

	@Override
	public String toString() {
		return String.format("Notification Id: %d, Message: %s, Student: %s\n", this.getId(), this.message, this.student.getUsername());
	}
}
