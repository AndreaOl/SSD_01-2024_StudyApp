package it.studyapp.application.view.admin;

import java.time.format.DateTimeFormatter;
import java.util.List;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import it.studyapp.application.entity.Session;
import it.studyapp.application.entity.Student;
import it.studyapp.application.entity.StudentGroup;
import it.studyapp.application.presenter.admin.AdminPresenter;
import it.studyapp.application.security.Roles;
import it.studyapp.application.view.layout.MainLayoutImpl;
import jakarta.annotation.security.RolesAllowed;

@PageTitle("Admin")
@Route(value = "admin", layout = MainLayoutImpl.class)
@RolesAllowed(Roles.ADMIN)
public class AdminViewImpl extends VerticalLayout implements AdminView {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private AdminPresenter presenter;
	
	private VerticalLayout grids;
			
	/*---------- Sessions Fields ----------*/
	private Grid<Session> sessionGrid = new Grid<>(Session.class);
	private HorizontalLayout sessionGridHeader = new HorizontalLayout();
	private H3 sessionCount = new H3();
	
	/*---------- Groups Fields ----------*/
	private Grid<StudentGroup> groupGrid = new Grid<>(StudentGroup.class);
	private HorizontalLayout groupGridHeader = new HorizontalLayout();
	private H3 groupCount = new H3();
	
	/*---------- Students Fields ----------*/
	private Grid<Student> studentGrid = new Grid<>(Student.class);
	private HorizontalLayout studentGridHeader = new HorizontalLayout();
	private H3 studentCount = new H3();
	
	
	public AdminViewImpl(AdminPresenter presenter) {
		setSizeFull();
		
		configureGrids();
		configureHeaders();
		
		grids = new VerticalLayout(sessionGridHeader, sessionGrid,
								   groupGridHeader, groupGrid,
								   studentGridHeader, studentGrid);
		grids.setSizeFull();
		add(grids);
		setHorizontalComponentAlignment(Alignment.STRETCH, grids);
		
		this.presenter = presenter;
		this.presenter.setView(this);
		this.presenter.updateSessionGrid();
		this.presenter.updateGroupGrid();
		this.presenter.updateStudentGrid();
	}
	
	
	private void configureHeaders() {
		configureSessionHeader();
		configureGroupHeader();
		configureStudentHeader();
	}
	
	private void configureGrids() {
		configureSessionGrid();
		configureGroupGrid();
		configureStudentGrid();
	}
	
	
	/*-------------------------------------------------------*
	 *                    Session Methods                    *
	 * ------------------------------------------------------*/
	
	private void configureSessionHeader() {

		/* ---------- Session Grid Header ---------- */

		H3 sessionTitle = new H3("Sessions");
		sessionCount.getStyle().setColor("grey");
		Button addSessionBtn = new Button("Add session", click -> {			
			presenter.createSession();
		});
		sessionGridHeader.setWidthFull();
		sessionGridHeader.add(sessionTitle, sessionCount, addSessionBtn);
		sessionGridHeader.setDefaultVerticalComponentAlignment(Alignment.CENTER);
		sessionGridHeader.expand(sessionCount);

	}

	private void configureSessionGrid() {

		/* ---------- Session Grid ---------- */

		sessionGrid.removeAllColumns();
		sessionGrid.addColumn(session -> session.getDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"))).setHeader("Date");
		sessionGrid.addColumn(Session::getLocation).setHeader("Location");
		sessionGrid.addColumn(Session::getSubject).setHeader("Subject");
		sessionGrid.addColumn(session -> session.getOwner().getUsername()).setHeader("Admin");
		sessionGrid.addColumn(session -> session.getParticipants().size()).setHeader("# Participants");
		sessionGrid.getColumns().forEach(col -> col.setAutoWidth(true));
		sessionGrid.setMinHeight("20%");
		sessionGrid.addItemClickListener(click -> {
			//presenter.onSessionClick(click.getItem());
		});

		sessionGrid.addItemDoubleClickListener(click -> {
			presenter.onSessionDoubleClick(click.getItem());
		});
	}
	
	@Override
	public void setSessionGridItems(List<Session> items) {
		sessionGrid.setItems(items);
	}
	
	@Override
	public void setSessionGridCount(int count) {
		sessionCount.setText("(" + count + ")");
	}
	
	
	
	/*-------------------------------------------------------*
	 *                    Group Methods                      *
	 * ------------------------------------------------------*/
	
	private void configureGroupHeader() {

		/* ---------- Group Grid Header ---------- */

		H3 groupTitle = new H3("Student Groups");
		groupCount.getStyle().setColor("grey");
		Button addGroupBtn = new Button("Add group", click -> {
			presenter.createGroup();
		});
		groupGridHeader.setWidthFull();
		groupGridHeader.add(groupTitle, groupCount, addGroupBtn);
		groupGridHeader.setDefaultVerticalComponentAlignment(Alignment.CENTER);
		groupGridHeader.expand(groupCount);

	}

	private void configureGroupGrid() {		
		
		/* ---------- Group Grid ---------- */

		groupGrid.setColumns("name");
		groupGrid.addColumn(group -> group.getOwner().getUsername()).setHeader("Admin");
		groupGrid.addColumn(group -> group.getMembers().size()).setHeader("# Members");
		groupGrid.getColumns().forEach(col -> col.setAutoWidth(true));
		groupGrid.setMinHeight("10%");
		groupGrid.addItemClickListener(click -> {
			//presenter.onGroupClick(click.getItem());
		});
		
		groupGrid.addItemDoubleClickListener(click -> {
			presenter.onGroupDoubleClick(click.getItem());
		});
	}
	
	@Override
	public void setGroupGridItems(List<StudentGroup> items) {
		groupGrid.setItems(items);
	}

	@Override
	public void setGroupGridCount(int count) {
		groupCount.setText("(" + count + ")");
	}
	
	
	
	/*-------------------------------------------------------*
	 *                    Student Methods                    *
	 * ------------------------------------------------------*/
	
	private void configureStudentHeader() {

		/* ---------- Student Grid Header ---------- */

		H3 studentTitle = new H3("Students");
		studentCount.getStyle().setColor("grey");
		studentGridHeader.setWidthFull();
		studentGridHeader.add(studentTitle, studentCount);
		studentGridHeader.setDefaultVerticalComponentAlignment(Alignment.CENTER);
		studentGridHeader.expand(studentCount);

	}

	private void configureStudentGrid() {

		/* ---------- Student Grid ---------- */

		studentGrid.removeAllColumns();
		studentGrid.addColumn(Student::getUsername).setHeader("Username");
		studentGrid.addColumn(Student::getEmail).setHeader("Email");
		studentGrid.addColumn(student -> student.getFirstName() + " " + student.getLastName()).setHeader("Name");
		studentGrid.addColumn(student -> student.getBirthDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))).setHeader("Date of Birth");
		studentGrid.addColumn(Student::getFieldOfStudy).setHeader("Field of Study");
		studentGrid.addColumn(Student::getYearFollowing).setHeader("Year Following");
		studentGrid.getColumns().forEach(col -> col.setAutoWidth(true));
		studentGrid.setMinHeight("20%");
		studentGrid.addItemClickListener(click -> {
			//presenter.onStudentClick(click.getItem());
		});

		studentGrid.addItemDoubleClickListener(click -> {
			presenter.onStudentDoubleClick(click.getItem());
		});
	}

	@Override
	public void setStudentGridItems(List<Student> items) {
		studentGrid.setItems(items);
	}

	@Override
	public void setStudentGridCount(int count) {
		studentCount.setText("(" + count + ")");
	}
	
}
