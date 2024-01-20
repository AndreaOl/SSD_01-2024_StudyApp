package it.studyapp.application.view.group;

import java.util.List;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import it.studyapp.application.entity.Student;
import it.studyapp.application.entity.StudentGroup;
import it.studyapp.application.presenter.group.GroupPresenter;
import it.studyapp.application.security.Roles;
import it.studyapp.application.view.layout.MainLayoutImpl;
import jakarta.annotation.security.RolesAllowed;

@PageTitle("Groups")
@Route(value = "groups", layout = MainLayoutImpl.class)
@RolesAllowed(Roles.USER)
public class GroupViewImpl extends VerticalLayout implements GroupView {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Grid<StudentGroup> groupGrid = new Grid<>(StudentGroup.class);
	private Grid<Student> membersGrid = new Grid<>(Student.class);
	private HorizontalLayout groupGridHeader = new HorizontalLayout();
	private HorizontalLayout membersGridHeader = new HorizontalLayout();
	private VerticalLayout grids;
	private H3 groupCount = new H3();
	private H3 membersCount = new H3();
	
	private GroupPresenter presenter;

	public GroupViewImpl(GroupPresenter presenter) {
		setSizeFull();      

		configureGrids();
		configureHeaders();

		grids = new VerticalLayout(groupGridHeader, groupGrid);
		grids.setSizeFull();
		add(grids);
		setHorizontalComponentAlignment(Alignment.STRETCH, grids);
		
		this.presenter = presenter;
		this.presenter.setView(this);
		this.presenter.updateGroupGrid();
	}

	private void configureHeaders() {

		/* ---------- Members Grid Header ---------- */

		H3 membersTitle = new H3("Members");
		membersCount.getStyle().setColor("grey");
		Button addSessionBtn = new Button("Add Session", click -> {
			presenter.createSession();
		});
		Button leaveGroupBtn = new Button("Leave Group", click -> {
			presenter.leaveGroup();
		});
		leaveGroupBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
		membersGridHeader.setWidthFull();
		membersGridHeader.add(membersTitle, membersCount, addSessionBtn, leaveGroupBtn);
		membersGridHeader.setDefaultVerticalComponentAlignment(Alignment.CENTER);
		membersGridHeader.expand(membersCount);

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


	private void configureGrids() {		
		
		/* ---------- Group Grid ---------- */

		groupGrid.setColumns("name");
		groupGrid.addColumn(group -> group.getOwner().getUsername()).setHeader("Admin");
		groupGrid.addColumn(group -> group.getMembers().size()).setHeader("# Members");
		groupGrid.getColumns().forEach(col -> col.setAutoWidth(true));
		groupGrid.setMinHeight("10%");
		groupGrid.addItemClickListener(click -> {
			presenter.onGroupClick(click.getItem());
		});
		
		groupGrid.addItemDoubleClickListener(click -> {
			presenter.onGroupDoubleClick(click.getItem());
		});
		
		
		/* ---------- Members Grid ---------- */
		
		membersGrid.removeAllColumns();
		membersGrid.addColumn(student -> student.getUsername()).setHeader("Username");
		membersGrid.addColumn(student -> student.getFieldOfStudy()).setHeader("Corso di Studi");		
		membersGrid.addColumn(student -> student.getYearFollowing()).setHeader("Anno frequentante");
		membersGrid.getColumns().forEach(col -> col.setAutoWidth(true));
		membersGrid.setMinHeight("20%");
		membersGrid.addItemDoubleClickListener(click -> {
			presenter.onMemberDoubleClick(click.getItem());
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

	@Override
	public void setMembersGridItems(List<Student> items) {
		membersGrid.setItems(items);
	}

	@Override
	public void setMembersGridCount(int count) {
		membersCount.setText("(" + count + ")");
	}

	@Override
	public void showMembers() {
		if(grids.getComponentCount() != 4)
			grids.add(membersGridHeader, membersGrid);		
	}

	@Override
	public void hideMembers() {
		if(grids.getComponentCount() == 4)
			grids.remove(grids.getComponentAt(2), grids.getComponentAt(3));		
	}

}
