package it.studyapp.application.ui.form.authentication;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.ValueContext;

import it.studyapp.application.presenter.authentication.PasswordPresenter;
import it.studyapp.application.security.CustomUserDetails;

public class PasswordFormBinder {

	private PasswordForm passwordForm;
	
	private PasswordPresenter presenter;
	
	/**
	 * Flag for disabling first run for password validation
	 */
	private boolean enablePasswordValidation;

	private boolean enablePasswordCheck;

	private String email;

	public PasswordFormBinder(PasswordForm passwordForm, PasswordPresenter presenter) {
		this.passwordForm = passwordForm;
		this.presenter = presenter;
		this.email = passwordForm.getEmail();
	}

	/**
	 * Method to add the data binding and validation logics
	 * to the registration form
	 */
	public void addBindingAndValidation() {
		BeanValidationBinder<CustomUserDetails> binder = new BeanValidationBinder<>(CustomUserDetails.class);
		//binder.bindInstanceFields(passwordForm);
		// A custom validator for password fields
		binder.forField(passwordForm.getPasswordField())
		.withValidator(this::passwordValidator).bind("password");
		
		if(email.isBlank()) {
			binder.forField(passwordForm.getOldpass())
			.withValidator(this::oldPassValidator).bind("password");
		}
		
		// The second password field is not connected to the Binder, but we
		// want the binder to re-check the password validator when the field
		// value changes. The easiest way is just to do that manually.
		passwordForm.getPasswordConfirmField().addValueChangeListener(e -> {
			// The user has modified the second field, now we can validate and show errors.
			// See passwordValidator() for how this flag is used.
			enablePasswordValidation = true;
			binder.validate();
		});
		
		passwordForm.getPasswordField().addValueChangeListener(e -> {
			// The user has modified the second field, now we can validate and show errors.
			// See passwordValidator() for how this flag is used.
			if(enablePasswordValidation) {
				binder.validate();
			}
		});
		
		// Set the label where bean-level error messages go
		binder.setStatusLabel(passwordForm.getErrorMessageField());
		enablePasswordCheck = false;
		// And finally the submit button
		passwordForm.getSubmitButton().addClickListener(event -> {
			enablePasswordCheck = true;
			
			if(email.isBlank())
				presenter.changePassword(passwordForm.getOldpass().getValue(), passwordForm.getPassword());
			else
				presenter.restorePassword(email, passwordForm.getPassword());
			
			// Show success message if everything went well
			showSuccess();
		});
	}

	/**
	 * Method to validate that:
	 * <p>
	 * 1) Password is at least 8 characters long
	 * <p>
	 * 2) Values in both fields match each other
	 */
	private ValidationResult passwordValidator(String pass1, ValueContext ctx) {
		/*
		 * Just a simple length check. A real version should check for password
		 * complexity as well!
		 */

		if (pass1 == null || pass1.length() < 8) {
			return ValidationResult.error("La password deve essere lunga almeno 8 caratteri");
		}

		if (!enablePasswordValidation) {
			// user hasn't visited the field yet, so don't validate just yet, but next time.
			return ValidationResult.ok();
		}

		String pass2 = passwordForm.getPasswordConfirmField().getValue();

		if (pass1 != null && pass1.equals(pass2)) {
			return ValidationResult.ok();
		}

		return ValidationResult.error("Le password non coincidono");
	}

	private ValidationResult oldPassValidator(String oldpass, ValueContext ctx) {
		if (!enablePasswordCheck) {
			// user hasn't visited the field yet, so don't validate just yet, but next time.
			return ValidationResult.ok();
		}
		
		if(presenter.passwordCheck(oldpass)) {
			return ValidationResult.ok();
		}
		
		enablePasswordCheck=false;
		return ValidationResult.error("Password errata");
	}
	
	/**
	 * We call this method when form submission has succeeded
	 */
	private void showSuccess() {
		Notification notification = new Notification();
		notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);

		notification = Notification.show("Nuova password salvata");

		// Here you'd typically redirect the user to another view
		UI ui = UI.getCurrent();
		ui.navigate("profile/me");
	}

}