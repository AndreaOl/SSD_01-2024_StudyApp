package it.studyapp.application.ui.form.profile;


import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;

import it.studyapp.application.event.ProfileUpdatedEvent;
import it.studyapp.application.presenter.profile.ProfilePresenter;
import it.studyapp.application.security.CustomUserDetails;

public class ProfileFormBinder {

	private ProfileForm profileForm;

	private ProfilePresenter presenter;

	public ProfileFormBinder(ProfileForm profileForm, ProfilePresenter presenter) {
		this.profileForm = profileForm;
		this.presenter = presenter;
	}

	/**
	 * Method to add the data binding and validation logics
	 * to the registration form
	 */
	public void addBindingAndValidation() {
		BeanValidationBinder<CustomUserDetails> binder = new BeanValidationBinder<>(CustomUserDetails.class);

		binder.bindInstanceFields(profileForm);

		// Set the label where bean-level error messages go
		binder.setStatusLabel(profileForm.getErrorMessageField());

		binder.forField(profileForm.getFirstName()).withValidator(
				value -> !profileForm.getFirstName().getValue().isBlank(), // Un validatore sempre valido, quindi non ci saranno errori predefiniti
				"Non deve essere vuoto"
				).bind("firstName"); 

		binder.forField(profileForm.getLastName()).withValidator(
				value -> !profileForm.getLastName().getValue().isBlank(), 
				"Non deve essere vuoto"
				).bind("lastName");

		binder.forField(profileForm.getFieldOfStudy()).withValidator(
				value -> !profileForm.getFieldOfStudy().getValue().isBlank(),
				"Inserisci il tuo corso di studi"
				).bind("fieldOfStudy");

		binder.forField(profileForm.getBirthDate()).withValidator(
				value -> profileForm.getBirthDate().getValue()!= null,
				"Inserisci una data di nascita valida"
				).bind("birthDate");

		binder.forField(profileForm.getYearFollowing()).withValidator(
				value -> profileForm.getYearFollowing().getValue()!= null,
				"Inserisci l'anno di frequenza del tuo corso di studi"
				).bind("yearFollowing");
		
		// And finally the submit button
		profileForm.getSubmitButton().addClickListener(event -> {
			try {
				// Create empty bean to store the details into
				CustomUserDetails userBean = new CustomUserDetails();


				// Run validators and write the values to the bean
				binder.writeBean(userBean);

				presenter.updateUser(userBean);
				
				profileForm.getSubmitButton().setEnabled(false);
				// Show success message if everything went well
				showSuccess();

				ComponentUtil.fireEvent(UI.getCurrent(), new ProfileUpdatedEvent(UI.getCurrent(), false, userBean.getUsername()));

			} catch (ValidationException exception) {

				// validation errors are already visible for each field,
				// and bean-level errors are shown in the status label.
				// We could show additional messages here if we want, do logging, etc.
			}
		});
	}

	private void showSuccess() {
		Notification notification = new Notification();
		notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
		notification = Notification.show("Data updated");
	}

}