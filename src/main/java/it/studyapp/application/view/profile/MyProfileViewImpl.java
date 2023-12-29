package it.studyapp.application.view.profile;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import it.studyapp.application.presenter.profile.ProfilePresenter;
import it.studyapp.application.ui.form.profile.ProfileForm;
import it.studyapp.application.ui.form.profile.ProfileFormBinder;
import it.studyapp.application.view.layout.MainLayoutImpl;
import jakarta.annotation.security.PermitAll;

@PageTitle("Profilo")
@Route(value = "profile/me", layout = MainLayoutImpl.class)
@PermitAll
public class MyProfileViewImpl extends VerticalLayout implements MyProfileView {

	private static final long serialVersionUID = 1L;
	private ProfilePresenter presenter;

	public MyProfileViewImpl(ProfilePresenter presenter){
		this.presenter = presenter;

		addClassName("profile-view");
		setSizeFull();
		setAlignItems(Alignment.CENTER);
		setJustifyContentMode(JustifyContentMode.CENTER);
		
		ProfileForm profileForm = new ProfileForm(this.presenter.getAuthenticatedUser(), true);
		setHorizontalComponentAlignment(Alignment.CENTER, profileForm);	
		Button button = new Button("Cambia la tua password", event -> {
			getUI().ifPresent(ui -> ui.navigate("changepassword"));
		});
		button.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		add(profileForm,button);
		
		ProfileFormBinder profileFormBinder = new ProfileFormBinder(profileForm, this.presenter);
		profileFormBinder.addBindingAndValidation();
	}
}