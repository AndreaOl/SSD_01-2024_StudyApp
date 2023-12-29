package it.studyapp.application.view.group;

import java.util.List;

import it.studyapp.application.entity.Student;
import it.studyapp.application.entity.StudentGroup;

public interface GroupView {
	
	public void setGroupGridItems(List<StudentGroup> items);
	public void setGroupGridCount(int count);
	public void setMembersGridItems(List<Student> items);
	public void setMembersGridCount(int count);
	public void showMembers();
	public void hideMembers();

}
