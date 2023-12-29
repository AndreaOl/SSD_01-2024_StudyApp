package it.studyapp.application.view.authentication;

import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import it.studyapp.application.entity.Token;
import it.studyapp.application.presenter.authentication.PasswordPresenter;
import it.studyapp.application.ui.form.authentication.EmailForm;
import it.studyapp.application.ui.form.authentication.EmailFormBinder;
import it.studyapp.application.ui.form.authentication.PasswordForm;
import it.studyapp.application.ui.form.authentication.PasswordFormBinder;

@Route("forgot")
@PageTitle("Recupera Password")
@AnonymousAllowed
public class ForgotPasswordViewImpl extends VerticalLayout implements ForgotPasswordView, HasUrlParameter<String>, AfterNavigationObserver {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private PasswordPresenter passwordPresenter;
	
	private String status;
	
	
	public ForgotPasswordViewImpl(PasswordPresenter passwordPresenter) {
		this.passwordPresenter = passwordPresenter;
		
		addClassName("forgot-password-view");
		setSizeFull();
		setAlignItems(Alignment.CENTER);
		setJustifyContentMode(JustifyContentMode.CENTER);	
	}
	
	@Override
	public void setParameter(BeforeEvent event, String parameter) {
		status = parameter;
	}
	
	@Override
	public void afterNavigation(AfterNavigationEvent event) {
		if(status.equalsIgnoreCase("success")) {

			H3 title2 = new H3("E' stata inviata una email di recupero della password all'indirizzo impostato ");
			H3 title3 = new H3("Controlla la tua cartella di spam");

			add(title2,title3);

		} else if(status.equalsIgnoreCase("new")) {

			H3 title1 = new H3("Inserisci l'email associata al tuo account");

			EmailForm emailForm = new EmailForm();
			setHorizontalComponentAlignment(Alignment.CENTER, emailForm);

			add(title1, emailForm);
			EmailFormBinder emailFormBinder = new EmailFormBinder(emailForm, passwordPresenter);
			emailFormBinder.addBindingAndValidation();
			
		} else {
			Token token = passwordPresenter.searchToken(status);
			if(token != null) {
				PasswordForm passwordForm = new PasswordForm(token.getEmail());
				setHorizontalComponentAlignment(Alignment.CENTER, passwordForm);
				H3 title = new H3("Cambia la tua password");
				add(title, passwordForm);

				PasswordFormBinder passwordFormBinder = new PasswordFormBinder(passwordForm, passwordPresenter);
				passwordFormBinder.addBindingAndValidation();

			} else {
				H3 title3 = new H3("Il link utilizzato non è valido");

				add(title3);
			}
		}
	}
}
