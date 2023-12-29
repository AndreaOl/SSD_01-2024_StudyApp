package it.studyapp.application.ui.form.authentication;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.ValueContext;

import it.studyapp.application.presenter.authentication.RegisterPresenter;
import it.studyapp.application.security.CustomUserDetails;

public class RegistrationFormBinder {

	private RegistrationForm registrationForm;

	private RegisterPresenter presenter;

	/**
	 * Flag for disabling first run for password validation
	 */
	private boolean enablePasswordValidation;

	public RegistrationFormBinder(RegistrationForm registrationForm, RegisterPresenter presenter) {
		this.registrationForm = registrationForm;
		this.presenter=presenter;
	}

	/**
	 * Method to add the data binding and validation logics
	 * to the registration form
	 */
	public void addBindingAndValidation() {
		BeanValidationBinder<CustomUserDetails> binder = new BeanValidationBinder<>(CustomUserDetails.class);
		binder.bindInstanceFields(registrationForm);

		// A custom validator for password fields
		binder.forField(registrationForm.getPasswordField())
		.withValidator(this::passwordValidator).bind("password");
		binder.forField(registrationForm.getUsername())
		.withValidator(this::userValidator).bind("username");
		binder.forField(registrationForm.getEmail())
		.withValidator(this::emailValidator).bind("email");

		binder.forField(registrationForm.getFirstName()).withValidator(
				value -> !registrationForm.getFirstName().getValue().isBlank(), // Un validatore sempre valido, quindi non ci saranno errori predefiniti
				"Non deve essere vuoto"
				).bind("firstName"); 
		binder.forField(registrationForm.getLastName()).withValidator(
				value -> !registrationForm.getLastName().getValue().isBlank(), 
				"Non deve essere vuoto"
				).bind("lastName");

		binder.forField(registrationForm.getFieldOfStudy()).withValidator(
				value -> !registrationForm.getFieldOfStudy().getValue().isBlank(),
				"Inserisci il tuo corso di studi"
				).bind("fieldOfStudy");

		binder.forField(registrationForm.getBirthDate()).withValidator(
				value -> registrationForm.getBirthDate().getValue()!= null,
				"Inserisci una data di nascita valida"
				).bind("birthDate");
		binder.forField(registrationForm.getYearFollowing()).withValidator(
				value -> registrationForm.getYearFollowing().getValue()!= null,
				"Inserisci l'anno di frequenza del tuo corso di studi"
				).bind("yearFollowing");

		// The second password field is not connected to the Binder, but we
		// want the binder to re-check the password validator when the field
		// value changes. The easiest way is just to do that manually.
		registrationForm.getPasswordConfirmField().addValueChangeListener(e -> {
			// The user has modified the second field, now we can validate and show errors.
			// See passwordValidator() for how this flag is used.
			enablePasswordValidation = true;

			binder.validate();
		});

		// Set the label where bean-level error messages go
		binder.setStatusLabel(registrationForm.getErrorMessageField());

		// And finally the submit button
		registrationForm.getSubmitButton().addClickListener(event -> {
			try {
				// Create empty bean to store the details into
				CustomUserDetails userBean = new CustomUserDetails();

				// Run validators and write the values to the bean
				binder.writeBean(userBean);


				// Typically, you would here call backend to store the bean
				presenter.createUser(userBean);

				// Show success message if everything went well
				showSuccess(userBean);
			} catch (ValidationException exception) {
				// validation errors are already visible for each field,
				// and bean-level errors are shown in the status label.
				// We could show additional messages here if we want, do logging, etc.
			}
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
			enablePasswordValidation = true;
			return ValidationResult.ok();
		}

		String pass2 = registrationForm.getPasswordConfirmField().getValue();

		if (pass1 != null && pass1.equals(pass2)) {
			return ValidationResult.ok();
		}

		return ValidationResult.error("Le password non coincidono");
	}

	private ValidationResult userValidator(String username, ValueContext ctx) {
		/*
		 * Just a simple length check. A real version should check for password
		 * complexity as well!
		 */

		if (username == null || username.length() < 4) {
			return ValidationResult.error("Username deve essere lungo almeno 4 caratteri");
		}

		if(!presenter.userExists(username)) {
			return ValidationResult.ok();
		}

		return ValidationResult.error("Username già preso.");
	}

	private ValidationResult emailValidator(String email, ValueContext ctx) {
		if(registrationForm.getEmail().getValue().isBlank()) {
			return ValidationResult.error("Inserisci l'email");
		}
		if(!presenter.emailExists(email)) {
			return ValidationResult.ok();
		}

		return ValidationResult.error("Email già presa.");
	}

	/**
	 * We call this method when form submission has succeeded
	 */
	private void showSuccess(CustomUserDetails userBean) {
		Notification notification = new Notification();
		notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);

		notification = Notification.show("Dati salvati, benvenuto " + userBean.getFirstName());
		UI ui= UI.getCurrent();
		ui.navigate("login");

		// Here you'd typically redirect the user to another view
	}

}