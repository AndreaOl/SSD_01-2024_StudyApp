package it.studyapp.application.presenter.admin;

import it.studyapp.application.entity.Session;
import it.studyapp.application.entity.Student;
import it.studyapp.application.entity.StudentGroup;
import it.studyapp.application.view.admin.AdminView;

public interface AdminPresenter {

	public void setView(AdminView view);
	
	public void updateSessionGrid();
	public void createSession();
	public void onSessionDoubleClick(Session session);
	
	public void createGroup();
	public void updateGroupGrid();
	public void onGroupDoubleClick(StudentGroup studentGroup);
	
	public void updateStudentGrid();
	public void onStudentDoubleClick(Student clickedStudent);
	
}
