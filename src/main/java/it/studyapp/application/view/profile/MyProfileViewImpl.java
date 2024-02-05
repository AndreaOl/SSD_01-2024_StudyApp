package it.studyapp.application.view.profile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import it.studyapp.application.presenter.profile.ProfilePresenter;
import it.studyapp.application.security.Roles;
import it.studyapp.application.ui.form.profile.ProfileForm;
import it.studyapp.application.ui.form.profile.ProfileFormBinder;
import it.studyapp.application.view.layout.MainLayoutImpl;
import jakarta.annotation.security.RolesAllowed;

@PageTitle("Profilo")
@Route(value = "profile/me", layout = MainLayoutImpl.class)
@RolesAllowed(Roles.USER)
public class MyProfileViewImpl extends VerticalLayout implements MyProfileView {

	private static final long serialVersionUID = 1L;
	private ProfilePresenter presenter;
	
	private final Logger logger = LoggerFactory.getLogger(MyProfileViewImpl.class);

	public MyProfileViewImpl(ProfilePresenter presenter){
		this.presenter = presenter;
		
		logger.info(UI.getCurrent() + ": Navigation to own profile");

		addClassName("profile-view");
		setSizeFull();
		setAlignItems(Alignment.CENTER);
		setJustifyContentMode(JustifyContentMode.CENTER);
		
		ProfileForm profileForm = new ProfileForm(this.presenter.getAuthenticatedUser(), true);
		setHorizontalComponentAlignment(Alignment.CENTER, profileForm);	
		Button button = new Button("Cambia la tua password", event -> {
			presenter.resetPassword();
			
			showSuccess();
		});
		button.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		add(profileForm,button);
		
		ProfileFormBinder profileFormBinder = new ProfileFormBinder(profileForm, this.presenter);
		profileFormBinder.addBindingAndValidation();
	}
	
	private void showSuccess() {
		Notification notification = new Notification();
		notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
		notification = Notification.show("An email was sent with a link to reset the password");
	}
}