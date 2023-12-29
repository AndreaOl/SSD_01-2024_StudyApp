package it.studyapp.application.ui.form.authentication;

import com.vaadin.flow.component.HasValueAndElement;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;

import java.time.LocalDate;
import java.util.stream.Stream;

public class RegistrationForm extends FormLayout {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private TextField username;
	private TextField firstName;
	private TextField lastName;

	private EmailField email;


	private TextField fieldOfStudy;
	private DatePicker birthDate;
	private ComboBox<String> yearFollowing;

	private PasswordField password;
	private PasswordField passwordConfirm;


	private Span errorMessageField;

	private Button submitButton;
	private static final String[] YEARS = {"1° Anno", "2° Anno", "3° Anno", "1° Anno Magistrale", "2° Anno Magistrale"};


	public RegistrationForm() {


		username = new TextField("Username");
		firstName = new TextField("Nome");
		lastName = new TextField("Cognome");
		email = new EmailField("Email");

		fieldOfStudy = new TextField("Campo di studi");
		birthDate = new DatePicker("Data di Nascita");
		birthDate.setMax(LocalDate.now().minusYears(10));

		yearFollowing = new ComboBox<>("Anno in cui segui", YEARS);
		yearFollowing.setAllowCustomValue(true);
		yearFollowing.addCustomValueSetListener(event -> yearFollowing.setValue(event.getDetail()));
		yearFollowing.setClearButtonVisible(true);
		yearFollowing.setAllowCustomValue(false);
		yearFollowing.setErrorMessage("Inserisci l'anno di frequenza del tuo corso");

		password = new PasswordField("Password");
		passwordConfirm = new PasswordField("Conferma password");

		errorMessageField = new Span();


		submitButton = new Button("Submit");
		submitButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		Hr empty= new Hr();
		empty.getElement().getStyle().set("background-color", "white");
		setRequiredIndicatorVisible(username, firstName, lastName, email, fieldOfStudy,
				yearFollowing, password, passwordConfirm);
		add(username, firstName, lastName, email, password,
				passwordConfirm, fieldOfStudy, yearFollowing, birthDate, errorMessageField,
				empty,
				submitButton);


		// Max width of the Form
		setMaxWidth("500px");

		// Allow the form layout to be responsive.
		// On device widths 0-490px we have one column.
		// Otherwise, we have two columns.
		setResponsiveSteps(
				new ResponsiveStep("0", 1, ResponsiveStep.LabelsPosition.TOP),
				new ResponsiveStep("490px", 2, ResponsiveStep.LabelsPosition.TOP));

		// These components always take full width
		setColspan(fieldOfStudy, 2);
		setColspan(username, 2);
		setColspan(email, 2);
		setColspan(errorMessageField, 2);
		setColspan(submitButton, 2);
		setColspan(empty, 2);
	}
	
	public static String[] getYears() {
		return YEARS;
	}

	//getters
	public TextField getUsername() {
		return username;
	}

	public PasswordField getPasswordField() { return password; }

	public PasswordField getPasswordConfirmField() { return passwordConfirm; }

	public Span getErrorMessageField() { return errorMessageField; }

	public Button getSubmitButton() { return submitButton; }

	public EmailField getEmail() {
		return email;
	}
	public TextField getFirstName() {
		return firstName;
	}
	public TextField getLastName() {
		return lastName;
	}
	public TextField getFieldOfStudy() {
		return fieldOfStudy;
	}
	public DatePicker getBirthDate() {
		return birthDate;
	}
	public ComboBox<String> getYearFollowing() {
		return yearFollowing;
	}
	public PasswordField getPassword() {
		return password;
	}
	public PasswordField getPasswordConfirm() {
		return passwordConfirm;
	}
	
	//setters
	public void setEmail(EmailField email) {
		this.email = email;
	}
	private void setRequiredIndicatorVisible(HasValueAndElement<?, ?>... components) {
		Stream.of(components).forEach(comp -> comp.setRequiredIndicatorVisible(true));
	}

	public void setErrorMessageField(Span errorMessageField) {
		this.errorMessageField = errorMessageField;
	}

}