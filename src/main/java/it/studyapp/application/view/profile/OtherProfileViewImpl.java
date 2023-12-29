package it.studyapp.application.view.profile;

import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import it.studyapp.application.entity.Student;
import it.studyapp.application.presenter.profile.ProfilePresenter;
import it.studyapp.application.ui.form.profile.ProfileForm;
import it.studyapp.application.ui.form.profile.ProfileFormBinder;
import it.studyapp.application.view.layout.MainLayoutImpl;
import jakarta.annotation.security.PermitAll;

@PageTitle("Profilo")
@Route(value = "profile", layout = MainLayoutImpl.class)
@PermitAll
public class OtherProfileViewImpl extends VerticalLayout implements OtherProfileView, HasUrlParameter<String>, AfterNavigationObserver {

	private static final long serialVersionUID = 1L;
	
	private ProfilePresenter presenter;
	
	private String username;

	public OtherProfileViewImpl(ProfilePresenter presenter)  {
		this.presenter = presenter;

		addClassName("profile-view");
		setSizeFull();
		setAlignItems(Alignment.CENTER);
		setJustifyContentMode(JustifyContentMode.CENTER);
	}
	
	@Override
	public void setParameter(BeforeEvent event, String parameter) {
		username = parameter;
	}
	
	@Override
	public void afterNavigation(AfterNavigationEvent event) {
		Student urlStudent = presenter.searchStudent(username);
		
		if(urlStudent != null) {
			
			H3 title = new H3("Profilo di " + username);

			ProfileForm profileForm = new ProfileForm(urlStudent, false);
			setHorizontalComponentAlignment(Alignment.CENTER, profileForm);

			add(title,profileForm);
			ProfileFormBinder profileFormBinder = new ProfileFormBinder(profileForm, presenter);

			profileFormBinder.addBindingAndValidation();
			
		} else {
			H3 title2 = new H3("L'utente che cerchi non esiste");
			add(title2);
		}
	}

}