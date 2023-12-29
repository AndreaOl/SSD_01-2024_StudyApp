package it.studyapp.application.presenter.group;

import it.studyapp.application.entity.Student;
import it.studyapp.application.entity.StudentGroup;
import it.studyapp.application.view.group.GroupView;

public interface GroupPresenter {
	
	public void setView(GroupView view);
	public void updateGroupGrid();
	public void createGroup();
	public void leaveGroup();
	public void onGroupClick(StudentGroup studentGroup);
	public void onGroupDoubleClick(StudentGroup studentGroup);
	public void onMemberDoubleClick(Student clickedStudent);
	public void createSession();
	
}