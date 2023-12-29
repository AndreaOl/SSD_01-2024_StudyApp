package it.studyapp.application.view.authentication;

import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import it.studyapp.application.presenter.authentication.PasswordPresenter;
import it.studyapp.application.ui.form.authentication.PasswordForm;
import it.studyapp.application.ui.form.authentication.PasswordFormBinder;
import it.studyapp.application.view.layout.MainLayoutImpl;
import jakarta.annotation.security.PermitAll;


@PageTitle("Cambia Password")
@Route(value = "changepassword", layout = MainLayoutImpl.class)
@PermitAll
public class ChangePasswordViewImpl extends VerticalLayout implements ChangePasswordView {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private PasswordPresenter presenter;
	
	public ChangePasswordViewImpl(PasswordPresenter presenter) { 
		this.presenter = presenter;
		
		addClassName("change-password-view");
		setSizeFull();
		setAlignItems(Alignment.CENTER);
		setJustifyContentMode(JustifyContentMode.CENTER);
		
		H3 title = new H3("Cambia la tua password");
		PasswordForm passwordForm = new PasswordForm("");
		setHorizontalComponentAlignment(Alignment.CENTER, passwordForm);

		add(title,passwordForm);

		PasswordFormBinder passwordFormBinder = new PasswordFormBinder(passwordForm, this.presenter);
		passwordFormBinder.addBindingAndValidation();
	
	}

}