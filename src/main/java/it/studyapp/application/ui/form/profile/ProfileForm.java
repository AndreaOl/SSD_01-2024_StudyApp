package it.studyapp.application.ui.form.profile;

import com.flowingcode.vaadin.addons.carousel.Carousel;
import com.flowingcode.vaadin.addons.carousel.Slide;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.HasValueAndElement;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding;

import it.studyapp.application.entity.Student;

import java.time.LocalDate;
import java.util.stream.Stream;

public class ProfileForm extends Composite<VerticalLayout> {

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
	private IntegerField avatar;

	private Span errorMessageField;

	private Button submitButton;
	private static final String[] YEARS = {"1° Anno", "2° Anno", "3° Anno", "1° Anno Magistrale", "2° Anno Magistrale"};
	private boolean firstTime = true;
	
	public ProfileForm(Student student, boolean modifiable) {

		avatar = new IntegerField("Avatar");
		username = new TextField("Username");
		username.setReadOnly(!modifiable);
		firstName = new TextField("Nome");
		firstName.setReadOnly(!modifiable);
		lastName = new TextField("Cognome");
		lastName.setReadOnly(!modifiable);
		email = new EmailField("Email");
		
		fieldOfStudy = new TextField("Campo di studi");
		fieldOfStudy.setReadOnly(!modifiable);
		birthDate = new DatePicker("Data di Nascita");
		birthDate.setReadOnly(!modifiable);
		birthDate.setMax(LocalDate.now().minusYears(10));
		submitButton = new Button("Submit");
		submitButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		
		yearFollowing = new ComboBox<>("Anno in cui segui", YEARS);
		yearFollowing.setAllowCustomValue(true);
		yearFollowing.addCustomValueSetListener(event -> yearFollowing.setValue(event.getDetail()));
		yearFollowing.setClearButtonVisible(true);
		yearFollowing.setReadOnly(!modifiable);
		yearFollowing.setAllowCustomValue(false);
		username.addValueChangeListener( event -> {
			submitButton.setEnabled(true);
		});
		firstName.addValueChangeListener( event -> {
			submitButton.setEnabled(true);
		});
		lastName.addValueChangeListener( event -> {
			submitButton.setEnabled(true);
		});
		email.addValueChangeListener( event -> {
			submitButton.setEnabled(true);
		});
		fieldOfStudy.addValueChangeListener( event -> {
			submitButton.setEnabled(true);
		});
		birthDate.addValueChangeListener( event -> {
			submitButton.setEnabled(true);
		});
		yearFollowing.addValueChangeListener( event -> {
			submitButton.setEnabled(true);
		});

		errorMessageField = new Span();

		Carousel c;
		if(modifiable) {
			
			Image img1 = new Image("images/user1.jpg","Avatar 1");
			img1.setWidth("200px");
			img1.setHeight("200px");
			
			Image img2 = new Image("images/user2.jpg","Avatar 2");
			img2.setWidth("200px");
			img2.setHeight("200px");
			
			Image img3 = new Image("images/user3.jpg","Avatar 3");
			img3.setWidth("200px");
			img3.setHeight("200px");
			
			Slide s1 = new Slide(img1);
			Slide s2 = new Slide(img2);
			Slide s3 = new Slide(img3);
			
			c = new Carousel(s1,s2,s3);
			c.setStartPosition(student.getAvatar());
			
		} else {
			Image imgSingle = new Image("images/user"+(student.getAvatar()+1)+".jpg","Avatar Fisso");
			imgSingle.setWidth("200px");
			imgSingle.setHeight("200px");
			Slide sSingle = new Slide(imgSingle);
			c = new Carousel(sSingle).withoutNavigation().withoutSwipe();
		}
		
		c.setWidth("210px");
		c.setHeight("210px");
		c.addChangeListener( e -> {
			if(firstTime) {
				firstTime = false;
				return;
			}
			submitButton.setEnabled(true);
			avatar.setValue(Integer.valueOf(e.getPosition()));
		});
		
		avatar.setValue(student.getAvatar());

		Hr empty= new Hr();
		empty.getElement().getStyle().set("background-color", "white");

		setRequiredIndicatorVisible(username, firstName, lastName, email, fieldOfStudy, yearFollowing);	


		setUsernameValue(student.getUsername());
		setBirthDateValue(student.getBirthDate());
		setEmailValue(student.getEmail());
		setFieldOfStudyValue(student.getFieldOfStudy());
		setFirstNameValue(student.getFirstName());
		setLastNameValue(student.getLastName());
		setYearFollowingValue(student.getYearFollowing());
		
		username.setReadOnly(true);
		email.setReadOnly(true);
		submitButton.setEnabled(false);	

		
		
		HorizontalLayout layoutRow = new HorizontalLayout();
		VerticalLayout layoutColumn5 = new VerticalLayout();
		VerticalLayout layoutColumn2 = new VerticalLayout();
		H3 h3 = new H3();
		HorizontalLayout layoutRow2 = new HorizontalLayout();
		VerticalLayout layoutColumn3 = new VerticalLayout();


		VerticalLayout layoutColumn4 = new VerticalLayout();
		HorizontalLayout layoutRow3 = new HorizontalLayout();

		HorizontalLayout layoutRow4 = new HorizontalLayout();
		Button buttonPrimary = new Button();
		Button buttonSecondary = new Button();
		VerticalLayout layoutColumn6 = new VerticalLayout();

		getContent().setWidthFull();
		getContent().addClassName(Padding.LARGE);
		getContent().setHeightFull();
		
		
		layoutRow.setWidthFull();
		getContent().setFlexGrow(1.0, layoutRow);
		layoutRow.setFlexGrow(1.0, layoutColumn5);
		layoutColumn5.setWidth(null);
		layoutRow.setFlexGrow(1.0, layoutColumn2);

		layoutColumn2.setHeightFull();
		layoutColumn2.setWidth(null);
		h3.setText("Personal Information");

		layoutRow2.setWidthFull();
		layoutRow2.addClassName(Gap.LARGE);
		layoutRow2.setFlexGrow(1.0, layoutColumn3);

		layoutColumn3.setWidth(null);
		firstName.setWidthFull();
		birthDate.setWidthFull();
		email.setWidthFull();

		layoutRow2.setFlexGrow(1.0, layoutColumn4);
		layoutColumn4.setWidth(null);

		lastName.setLabel("Last Name");
		lastName.setWidthFull();

		layoutRow3.addClassName(Gap.LARGE);
		layoutRow3.setWidthFull();
		layoutRow3.setFlexGrow(1.0, yearFollowing);
		fieldOfStudy.setWidthFull();

		layoutRow4.addClassName(Gap.LARGE);
		buttonPrimary.setText("Save");
		buttonPrimary.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		buttonSecondary.setText("Cancel");
		layoutRow.setFlexGrow(5.0, layoutColumn6);
		layoutColumn6.setWidth(null);

        // Aggiungi il Div al VerticalLayout esistente
		getContent().add(layoutRow);
		layoutRow.add(layoutColumn5);
		layoutRow.add(layoutColumn2);
		layoutColumn5.add(c);
		layoutColumn2.add(h3);
		layoutColumn2.add(layoutRow2);
		layoutRow2.add(layoutColumn3);

		layoutColumn3.add(firstName);
		layoutColumn3.add(birthDate);
		layoutColumn3.add(email);
		layoutRow2.add(layoutColumn4);
		layoutColumn4.add(lastName);
		layoutColumn4.add(layoutRow3);
		layoutRow3.add(yearFollowing);

		layoutColumn4.add(fieldOfStudy);
		layoutColumn2.add(layoutRow4);


		if(modifiable) {
			layoutRow4.add(submitButton);
		}

		layoutRow.add(layoutColumn6);
	}

	public TextField getUsername() {
		return username;
	}


	public Span getErrorMessageField() { return errorMessageField; }

	public Button getSubmitButton() { return submitButton; }

	private void setRequiredIndicatorVisible(HasValueAndElement<?, ?>... components) {
		Stream.of(components).forEach(comp -> comp.setRequiredIndicatorVisible(true));
	}

	public IntegerField getAvatar() {
		return avatar;
	}

	public void setAvatar(IntegerField avatar) {
		this.avatar = avatar;
	}

	public String getFirstNameValue() {
		return firstName.getValue();
	}

	public void setFirstNameValue(String firstName) {
		this.firstName.setValue(firstName);
	}

	public String getLastNameValue() {
		return lastName.getValue();
	}

	public void setLastNameValue(String lastName) {
		this.lastName.setValue(lastName);
	}

	public String getEmailValue() {
		return email.getValue();
	}

	public void setEmailValue(String email) {
		this.email.setValue(email);
	}

	public String getFieldOfStudyValue() {
		return fieldOfStudy.getValue();
	}

	public void setFieldOfStudyValue(String fieldOfStudy) {
		this.fieldOfStudy.setValue(fieldOfStudy);
	}

	public LocalDate getBirthDateValue() {
		return birthDate.getValue();
	}

	public void setBirthDateValue(LocalDate birthDate) {
		this.birthDate.setValue(birthDate); 
	}

	public String getYearFollowingValue() {
		return yearFollowing.getValue();
	}

	public void setYearFollowingValue(String yearFollowing) {
		this.yearFollowing.setValue(yearFollowing);
	}

	public void setUsernameValue(String username) {
		this.username.setValue(username);
	}

	public void setErrorMessageField(Span errorMessageField) {
		this.errorMessageField = errorMessageField;
	}

	public TextField getFirstName() {
		return firstName;
	}

	public TextField getLastName() {
		return lastName;
	}

	public EmailField getEmail() {
		return email;
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

}