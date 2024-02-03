package it.studyapp.application.ui.dialog;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox.ItemFilter;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.dialog.DialogVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.function.SerializableBiConsumer;
import com.vaadin.flow.function.SerializableConsumer;

import it.studyapp.application.entity.Session;
import it.studyapp.application.entity.Student;
import it.studyapp.application.entity.StudentGroup;
import it.studyapp.application.security.Roles;
import it.studyapp.application.security.SecurityService;
import it.studyapp.application.service.DataService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SessionDialog extends Dialog {
	private static final long serialVersionUID = 1L;

    private final DataService dataService;
    private final SecurityService securityService;
    
    private final VerticalLayout componentsLayout;
    private final TextField fieldSubject;
    private final TextField fieldLocation;
    private final DateTimePicker fieldDate;
    private final MultiSelectComboBox<Student> fieldParticipants;
    private final Binder<Session> binder;
    
    private SerializableBiConsumer<Session, Set<Student>> onSaveBiConsumer;
    private SerializableConsumer<Session> onRemoveConsumer;
    
    private Session gridSession;
    private StudentGroup studentGroup;

    public SessionDialog(DataService dataService, SecurityService securityService, Session gridSession, StudentGroup studentGroup) {
    	this.dataService = dataService;
    	this.securityService = securityService;
    	
    	this.gridSession = gridSession;
    	this.studentGroup = studentGroup;

        setCloseOnEsc(true);
        setCloseOnOutsideClick(true);

        addThemeVariants(DialogVariant.LUMO_NO_PADDING);
        setWidth("500px");

        fieldSubject = new TextField("Subject");
        fieldLocation = new TextField("Location");
        fieldDate = new DateTimePicker("Session date");
        fieldDate.setMin(LocalDateTime.now());
        
        /* Setup participants selection */
        ItemFilter<Student> filter = (student, filterString) -> 
        				(student.getUsername() + " " + student.getFirstName() + " " + student.getLastName())
                        .toLowerCase().indexOf(filterString.toLowerCase()) > -1;
                        
        fieldParticipants = new MultiSelectComboBox<>("Invite other students");
        List<Student> students = new ArrayList<>(dataService.findAllStudents().stream().filter(s -> !s.hasRole(Roles.ADMIN)).toList());
        students.remove(dataService.searchStudent(securityService.getAuthenticatedUser().getUsername()).get(0));
        fieldParticipants.setItems(filter, students);
        fieldParticipants.setItemLabelGenerator(Student::getUsername);
        fieldParticipants.setRenderer(createRenderer());
        fieldParticipants.getStyle().set("--vaadin-multi-select-combo-box-width", "16em");

        componentsLayout = new VerticalLayout(fieldSubject, fieldLocation, fieldDate, fieldParticipants);
        componentsLayout.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.STRETCH);
        componentsLayout.setSizeFull();
        componentsLayout.setSpacing(false);

        binder = new Binder<>(Session.class);
        binder.forField(fieldLocation).asRequired().bind(Session::getLocation, Session::setLocation);
        binder.forField(fieldDate).asRequired().bind(Session::getDate, Session::setDate);
        binder.forField(fieldSubject).asRequired().bind(Session::getSubject, Session::setSubject);

        /* Setup buttons */
        Button buttonSave = new Button("Save");
        buttonSave.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonSave.addClickListener(e -> onSave());

        Button buttonCancel = new Button("Cancel", e -> close());
        buttonCancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        HorizontalLayout buttons = new HorizontalLayout(buttonSave, buttonCancel);
        buttons.setPadding(true);
        buttons.getStyle().set("border-top", "1px solid #ddd");

        /* Session already exists */
        if(this.gridSession != null) {
            Button buttonRemove = new Button("Remove", e -> onRemove());
            buttonRemove.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR);
            buttons.add(buttonRemove);
            
            binder.readBean(this.gridSession);
            
            List<Student> participants = new ArrayList<>(this.gridSession.getParticipants());
            participants.remove(dataService.searchStudent(securityService.getAuthenticatedUser().getUsername()).get(0));
            fieldParticipants.select(participants);
        }
        
        /* Session from group */
        if(this.studentGroup != null) {
        	List<Student> participants = new ArrayList<>(this.studentGroup.getMembers());
        	participants.remove(dataService.searchStudent(securityService.getAuthenticatedUser().getUsername()).get(0));
        	fieldParticipants.select(participants);
        	fieldParticipants.setEnabled(false);
        }

        Scroller scroller = new Scroller(componentsLayout);
        VerticalLayout outer = new VerticalLayout();
        outer.add(scroller, buttons);
        outer.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.STRETCH);
        outer.setFlexGrow(1, scroller);
        outer.setSizeFull();
        outer.setPadding(false);
        outer.setSpacing(false);

        add(outer);

        fieldSubject.focus();
    }

	private void onSave() {		
		try {
			Session session = this.gridSession != null ? this.gridSession : new Session();
			binder.writeBean(session);
			
			/* New Session */
			if(this.gridSession == null)
				session.setOwner(dataService.searchStudent(securityService.getAuthenticatedUser().getUsername()).get(0));

			if(onSaveBiConsumer != null) onSaveBiConsumer.accept(session, fieldParticipants.getSelectedItems());
			close();
		} catch (ValidationException e) {
		}
    }

    private void onRemove() {
    	if(onRemoveConsumer != null) onRemoveConsumer.accept(this.gridSession);
        close();
    }    

    private Renderer<Student> createRenderer() {
        StringBuilder tpl = new StringBuilder();
        tpl.append("<div style=\"display: flex;\">");
        tpl.append(
                "  <img style=\"height: var(--lumo-size-m); margin-right: var(--lumo-space-s);\" src=\"${item.pictureUrl}\" alt=\"Portrait of ${item.username}\" />");
        tpl.append("  <div>");
        tpl.append("    ${item.username}");
        tpl.append(
                "    <div style=\"font-size: var(--lumo-font-size-s); color: var(--lumo-secondary-text-color);\">${item.firstName} ${item.lastName}</div>");
        tpl.append("  </div>");
        tpl.append("</div>");

        return LitRenderer.<Student> of(tpl.toString())
                .withProperty("pictureUrl", Student::getIconUrl)
                .withProperty("firstName", Student::getFirstName)
                .withProperty("lastName", Student::getLastName)
                .withProperty("username", Student::getUsername);
	}

	public void setOnSaveBiConsumer(SerializableBiConsumer<Session, Set<Student>> onSaveBiConsumer) {
		this.onSaveBiConsumer = onSaveBiConsumer;
	}

	public void setOnRemoveConsumer(SerializableConsumer<Session> onRemoveConsumer) {
		this.onRemoveConsumer = onRemoveConsumer;
	}

}
