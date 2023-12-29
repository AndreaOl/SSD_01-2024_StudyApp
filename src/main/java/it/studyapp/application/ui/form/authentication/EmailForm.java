package it.studyapp.application.ui.form.authentication;

import com.vaadin.flow.component.HasValueAndElement;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.textfield.EmailField;

import java.util.stream.Stream;

public class EmailForm extends FormLayout {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	private EmailField email;


	private Span errorMessageField;

	private Button submitButton;


	public EmailForm() {

		email = new EmailField("Email");

		errorMessageField = new Span();


		submitButton = new Button("Submit");
		submitButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		Hr empty= new Hr();
		empty.getElement().getStyle().set("background-color", "white");
		setRequiredIndicatorVisible(email);
		add(email,submitButton);


		// Max width of the Form
		setMaxWidth("500px");

		// Allow the form layout to be responsive.
		// On device widths 0-490px we have one column.
		// Otherwise, we have two columns.
		setResponsiveSteps(
				new ResponsiveStep("0", 1, ResponsiveStep.LabelsPosition.TOP),
				new ResponsiveStep("490px", 2, ResponsiveStep.LabelsPosition.TOP));

		// These components always take full width

		setColspan(email, 2);
		setColspan(errorMessageField, 2);
		setColspan(submitButton, 2);
	}
	public EmailField getEmail() {
		return email;
	}
	public Span getErrorMessageField() {
		return errorMessageField;
	}
	public Button getSubmitButton() { return submitButton; }

	private void setRequiredIndicatorVisible(HasValueAndElement<?, ?>... components) {
		Stream.of(components).forEach(comp -> comp.setRequiredIndicatorVisible(true));
	}

	public void setErrorMessageField(Span errorMessageField) {
		this.errorMessageField = errorMessageField;
	}

}