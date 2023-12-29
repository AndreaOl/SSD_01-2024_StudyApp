package it.studyapp.application.view.authentication;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;


@Route("login")
@PageTitle("Login")
@AnonymousAllowed
public class LoginView extends VerticalLayout implements BeforeEnterObserver {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final LoginForm login = new LoginForm();

	public LoginView(){

		addClassName("login-view");
		setSizeFull();
		setAlignItems(Alignment.CENTER);
		setJustifyContentMode(JustifyContentMode.CENTER);

		login.setAction("login");
		login.addForgotPasswordListener(event -> {
			getUI().ifPresent(ui -> ui.navigate("forgot/new"));
		});

		Button button = new Button("Registrati Ora", event -> {
			getUI().ifPresent(ui -> ui.navigate("register"));
		});
		button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		button.setWidth("311px");
		button.setHeight("36px");
		add(
				new H1("Login now"),
				login,
				button
				);

	}

	@Override
	public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
		// inform the user about an authentication error
		if(beforeEnterEvent.getLocation()  
				.getQueryParameters()
				.getParameters()
				.containsKey("error")) {
			login.setError(true);
		}
	}
}