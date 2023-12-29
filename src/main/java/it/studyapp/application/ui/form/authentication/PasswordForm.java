package it.studyapp.application.ui.form.authentication;

import com.vaadin.flow.component.HasValueAndElement;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.textfield.PasswordField;

import java.util.stream.Stream;

public class PasswordForm extends FormLayout {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private PasswordField oldpass;

	private PasswordField password;
	private PasswordField passwordConfirm;

	private Button submitButton;
	private Span errorMessageField;
	private String email;
	
	public PasswordForm(String email) {

		this.email=email;
		oldpass = new PasswordField("Vecchia Password");
		password = new PasswordField("Password");
		passwordConfirm = new PasswordField("Conferma password");

		errorMessageField = new Span();


		submitButton = new Button("Submit");
		submitButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		Hr empty= new Hr();
		empty.getElement().getStyle().set("background-color", "white");

		setRequiredIndicatorVisible(oldpass, password, passwordConfirm);
		if(email.isBlank()) {
			add(oldpass, password, passwordConfirm, errorMessageField, empty,
					submitButton);
		}else {
			add(password, passwordConfirm, errorMessageField, empty,
					submitButton);
		}

		// Max width of the Form
		setMaxWidth("500px");

		// Allow the form layout to be responsive.
		// On device widths 0-490px we have one column.
		// Otherwise, we have two columns.
		setResponsiveSteps(
				new ResponsiveStep("0", 1, ResponsiveStep.LabelsPosition.TOP),
				new ResponsiveStep("490px", 2, ResponsiveStep.LabelsPosition.TOP));

		// These components always take full width
		setColspan(oldpass, 2);
		setColspan(submitButton, 2);
		setColspan(empty, 2);
	}

	public PasswordField getOldpass() {
		return oldpass;
	}

	public void setOldpass(PasswordField oldpass) {
		this.oldpass = oldpass;
	}

	public PasswordField getPasswordField() { return password; }

	public PasswordField getPasswordConfirmField() { return passwordConfirm; }

	public Span getErrorMessageField() { return errorMessageField; }

	public Button getSubmitButton() { return submitButton; }

	private void setRequiredIndicatorVisible(HasValueAndElement<?, ?>... components) {
		Stream.of(components).forEach(comp -> comp.setRequiredIndicatorVisible(true));
	}

	public String getPassword() {
		return password.getValue();
	}

	public void setPassword(String password) {
		this.password.setValue(password);
	}

	public String getPasswordConfirm() {
		return passwordConfirm.getValue();
	}

	public void setPasswordConfirm(String passwordConfirm) {
		this.passwordConfirm.setValue(passwordConfirm);
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setErrorMessageField(Span errorMessageField) {
		this.errorMessageField = errorMessageField;
	}

}