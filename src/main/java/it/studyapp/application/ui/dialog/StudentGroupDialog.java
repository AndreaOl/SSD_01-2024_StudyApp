package it.studyapp.application.ui.dialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.combobox.ComboBox.ItemFilter;
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

import it.studyapp.application.entity.Student;
import it.studyapp.application.entity.StudentGroup;
import it.studyapp.application.security.SecurityService;
import it.studyapp.application.service.DataService;

public class StudentGroupDialog extends Dialog {
	private static final long serialVersionUID = 1L;
	
	private final DataService dataService;
    private final SecurityService securityService;
    
    private final VerticalLayout componentsLayout;
    private final TextField fieldName;
    private final MultiSelectComboBox<Student> fieldMembers;
    private final Binder<StudentGroup> binder;
    
    private SerializableBiConsumer<StudentGroup, Set<Student>> onSaveBiConsumer;
    private SerializableConsumer<StudentGroup> onRemoveConsumer;
    
    private StudentGroup gridGroup;
    
    public StudentGroupDialog(DataService dataService, SecurityService securityService, StudentGroup gridGroup) {
    	this.dataService = dataService;
    	this.securityService = securityService;
    	this.gridGroup = gridGroup;
    	
    	setCloseOnEsc(true);
        setCloseOnOutsideClick(true);

        addThemeVariants(DialogVariant.LUMO_NO_PADDING);
        setWidth("500px");
        
        fieldName = new TextField("Name");
        
        /* Setup members selection */
        ItemFilter<Student> filter = (student, filterString) -> 
        				(student.getUsername() + " " + student.getFirstName() + " " + student.getLastName())
                        .toLowerCase().indexOf(filterString.toLowerCase()) > -1;
                        
        fieldMembers = new MultiSelectComboBox<>("Invite other students");
        List<Student> students = new ArrayList<>(dataService.findAllStudents());
        students.remove(dataService.searchStudent(securityService.getAuthenticatedUser().getUsername()).get(0));
        fieldMembers.setItems(filter, students);
        fieldMembers.setItemLabelGenerator(Student::getUsername);
        fieldMembers.setRenderer(createRenderer());
        fieldMembers.getStyle().set("--vaadin-multi-select-combo-box-width", "16em");
        
        componentsLayout = new VerticalLayout(fieldName, fieldMembers);
        componentsLayout.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.STRETCH);
        componentsLayout.setSizeFull();
        componentsLayout.setSpacing(false);
        
        binder = new Binder<>(StudentGroup.class);
        binder.forField(fieldName).asRequired().bind(StudentGroup::getName, StudentGroup::setName);
    
        /* Setup buttons */
        Button buttonSave = new Button("Save");
        buttonSave.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonSave.addClickListener(e -> onSave());

        Button buttonCancel = new Button("Cancel", e -> close());
        buttonCancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        HorizontalLayout buttons = new HorizontalLayout(buttonSave, buttonCancel);
        buttons.setPadding(true);
        buttons.getStyle().set("border-top", "1px solid #ddd");
        
        /* Group already exists */
        if(this.gridGroup != null) {
        	Button buttonRemove = new Button("Remove", e -> onRemove());
            buttonRemove.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR);
            buttons.add(buttonRemove);
            
            binder.readBean(this.gridGroup);
            
            List<Student> members = new ArrayList<>(this.gridGroup.getMembers());
            members.remove(dataService.searchStudent(securityService.getAuthenticatedUser().getUsername()).get(0));
            fieldMembers.select(members);
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

        fieldName.focus();
    }
    
    private void onSave() {
    	try {
    		StudentGroup studentGroup = this.gridGroup != null ? this.gridGroup : new StudentGroup();
    		binder.writeBean(studentGroup);
    		
    		/* New Student Group */
    		if(this.gridGroup == null)
    			studentGroup.setOwner(dataService.searchStudent(securityService.getAuthenticatedUser().getUsername()).get(0));
    		
    		if(onSaveBiConsumer != null) onSaveBiConsumer.accept(studentGroup, fieldMembers.getSelectedItems());
    		close();
    	} catch (ValidationException e) {
		}
    }
    
    private void onRemove() {
    	if(onRemoveConsumer != null) onRemoveConsumer.accept(this.gridGroup);
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
    
	public void setOnSaveBiConsumer(SerializableBiConsumer<StudentGroup, Set<Student>> onSaveBiConsumer) {
		this.onSaveBiConsumer = onSaveBiConsumer;
	}

	public void setOnRemoveConsumer(SerializableConsumer<StudentGroup> onRemoveConsumer) {
		this.onRemoveConsumer = onRemoveConsumer;
	}

}
