package it.studyapp.application.view.authentication;

import it.studyapp.application.presenter.authentication.RegisterPresenter;
import it.studyapp.application.ui.form.authentication.RegistrationForm;
import it.studyapp.application.ui.form.authentication.RegistrationFormBinder;

import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;


import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;


@Route("register")
@PageTitle("Register")
@AnonymousAllowed
public class RegisterViewImpl extends VerticalLayout implements RegisterView {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private RegisterPresenter registerPresenter;

	public RegisterViewImpl(RegisterPresenter registerPresenter) {  
		this.registerPresenter = registerPresenter;
		
		setSizeFull();
		setAlignItems(Alignment.CENTER);
		setJustifyContentMode(JustifyContentMode.CENTER);
		H3 title = new H3("Iscriviti ora");
		RegistrationForm registrationForm = new RegistrationForm();
		setHorizontalComponentAlignment(Alignment.CENTER, registrationForm);
		add(title,registrationForm);
		RegistrationFormBinder registrationFormBinder = new RegistrationFormBinder(registrationForm, this.registerPresenter);
		registrationFormBinder.addBindingAndValidation();
	}

}