package it.studyapp.application.view.admin;

import java.util.List;

import it.studyapp.application.entity.Session;
import it.studyapp.application.entity.Student;
import it.studyapp.application.entity.StudentGroup;

public interface AdminView {
	
	public void setSessionGridItems(List<Session> items);
	public void setSessionGridCount(int count);
	public void setGroupGridItems(List<StudentGroup> items);
	public void setGroupGridCount(int count);
	public void setStudentGridItems(List<Student> items);
	public void setStudentGridCount(int count);
	
}
