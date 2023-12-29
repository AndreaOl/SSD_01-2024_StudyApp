package it.studyapp.application.ui.form.authentication;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.data.binder.BeanValidationBinder;

import it.studyapp.application.presenter.authentication.PasswordPresenter;
import it.studyapp.application.security.CustomUserDetails;

public class EmailFormBinder {

	private EmailForm emailForm;

	private PasswordPresenter presenter;

	public EmailFormBinder(EmailForm emailForm, PasswordPresenter presenter) {
		this.emailForm = emailForm;
		this.presenter = presenter;
	}

	/**
	 * Method to add the data binding and validation logics
	 * to the registration form
	 */
	public void addBindingAndValidation() {
		BeanValidationBinder<CustomUserDetails> binder = new BeanValidationBinder<>(CustomUserDetails.class);
		binder.bindInstanceFields(emailForm);
		binder.forField(emailForm.getEmail()).withValidator(
			    value -> !emailForm.getEmail().getValue().isBlank() || binder.isValid(), // Un validatore sempre valido, quindi non ci saranno errori predefiniti
			    "Inserisci una email valida"
			).bind("email");

		// Set the label where bean-level error messages go
		binder.setStatusLabel(emailForm.getErrorMessageField());
		
		// And finally the submit button
		emailForm.getSubmitButton().addClickListener(event -> {
			if(!binder.validate().isOk())
				return;
				
			String mail = emailForm.getEmail().getValue();
			presenter.sendEmail(mail);
			
			UI.getCurrent().getPage().setLocation("forgot/success");	
		});
	}
}

