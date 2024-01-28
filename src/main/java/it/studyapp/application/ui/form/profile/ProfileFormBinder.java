package it.studyapp.application.ui.form.profile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;

import it.studyapp.application.Application;
import it.studyapp.application.entity.Student;
import it.studyapp.application.event.ProfileUpdatedEvent;
import it.studyapp.application.presenter.profile.ProfilePresenter;

public class ProfileFormBinder {
	
	private final Logger logger = LoggerFactory.getLogger(ProfileFormBinder.class);

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
		BeanValidationBinder<Student> binder = new BeanValidationBinder<>(Student.class);

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
				Student userBean = profileForm.getStudent();


				// Run validators and write the values to the bean
				binder.writeBean(userBean);
				
				logger.info("Updating user " + userBean.getUsername());

				presenter.updateUser(userBean);
				
				profileForm.getSubmitButton().setEnabled(false);
				// Show success message if everything went well
				showSuccess();

				ComponentUtil.fireEvent(UI.getCurrent(), new ProfileUpdatedEvent(UI.getCurrent(), false, userBean.getUsername()));

				UI ui = Application.getUserUI("admin");
				if(ui != null)
					ui.access(() -> ComponentUtil.fireEvent(ui, new ProfileUpdatedEvent(UI.getCurrent(), false, userBean.getUsername())));
				
			} catch (ValidationException exception) {
				logger.error("Something went wrong trying to update user " + profileForm.getStudent().getUsername());
				// validation errors are already visible for each field,
				// and bean-level errors are shown in the status label.
				// We could show additional messages here if we want, do logging, etc.
			}
		});
	}

	private void showSuccess() {
		Notification notification = new Notification();
		notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
		notification = Notification.show("Profile updated");
	}

}