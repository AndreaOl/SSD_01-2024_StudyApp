package it.studyapp.application.view.session;

import java.time.format.DateTimeFormatter;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import it.studyapp.application.entity.Session;
import it.studyapp.application.entity.Student;
import it.studyapp.application.presenter.session.SessionPresenter;
import it.studyapp.application.security.Roles;
import it.studyapp.application.view.layout.MainLayoutImpl;
import jakarta.annotation.security.RolesAllowed;


@PageTitle("Sessions")
@Route(value = "sessions", layout = MainLayoutImpl.class)
@RolesAllowed(Roles.USER)
public class SessionViewImpl extends VerticalLayout implements SessionView {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final Logger logger = LoggerFactory.getLogger(SessionViewImpl.class);
			
	private Grid<Session> sessionGrid = new Grid<>(Session.class);
	private Grid<Student> participantsGrid = new Grid<>(Student.class);
	private HorizontalLayout participantsGridHeader = new HorizontalLayout();
	private HorizontalLayout sessionGridHeader = new HorizontalLayout();
	private VerticalLayout grids;
	private H3 sessionCount = new H3();
	private H3 participantsCount = new H3();
	
	private SessionPresenter presenter;

	public SessionViewImpl(SessionPresenter presenter) {		
		setSizeFull();

		configureGrids();
		configureHeaders();

		grids = new VerticalLayout(sessionGridHeader, sessionGrid);
		grids.setSizeFull();
		add(grids);
		setHorizontalComponentAlignment(Alignment.STRETCH, grids);
		
		this.presenter = presenter;
		this.presenter.setView(this);
		
		logger.info(UI.getCurrent() + ": Navigation to Sessions page");
		
		this.presenter.updateSessionGrid();
	}


	private void configureHeaders() {

		/* ---------- Members Grid Header ---------- */

		H3 participantsTitle = new H3("Participants");
		participantsCount.getStyle().setColor("grey");
		Button leaveSessionBtn = new Button("Leave Session", click -> {
			presenter.leaveSession();
		});
		leaveSessionBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
		participantsGridHeader.setWidthFull();
		participantsGridHeader.add(participantsTitle, participantsCount, leaveSessionBtn);
		participantsGridHeader.setDefaultVerticalComponentAlignment(Alignment.CENTER);
		participantsGridHeader.expand(participantsCount);


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


	private void configureGrids() {

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
			presenter.onSessionClick(click.getItem());
		});

		sessionGrid.addItemDoubleClickListener(click -> {
			presenter.onSessionDoubleClick(click.getItem());
		});

		participantsGrid.removeAllColumns();
		participantsGrid.addColumn(student -> student.getUsername()).setHeader("Username");
		participantsGrid.addColumn(student -> student.getFieldOfStudy()).setHeader("Corso di Studi");		
		participantsGrid.addColumn(student -> student.getYearFollowing()).setHeader("Anno frequentante");
		participantsGrid.getColumns().forEach(col -> col.setAutoWidth(true));
		participantsGrid.setMinHeight("20%");
		participantsGrid.addItemDoubleClickListener(click -> {
			presenter.onParticipantDoubleClick(click.getItem());
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
	
	@Override
	public void setParticipantsGridItems(List<Student> items) {
		participantsGrid.setItems(items);
	}
	
	@Override
	public void setParticipantsGridCount(int count) {
		participantsCount.setText("(" + count + ")");
	}
	
	@Override
	public void showParticipants() {
		if(grids.getComponentCount() != 4)
			grids.add(participantsGridHeader, participantsGrid);
	}
	
	@Override
	public void hideParticipants() {
		if(grids.getComponentCount() == 4)
			grids.remove(grids.getComponentAt(2), grids.getComponentAt(3));
	}

}