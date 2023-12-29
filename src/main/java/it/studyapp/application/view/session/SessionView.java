package it.studyapp.application.view.session;

import java.util.List;

import it.studyapp.application.entity.Session;
import it.studyapp.application.entity.Student;

public interface SessionView {

	public void setSessionGridItems(List<Session> items);
	public void setSessionGridCount(int count);
	public void setParticipantsGridItems(List<Student> items);
	public void setParticipantsGridCount(int count);
	public void showParticipants();
	public void hideParticipants();
	
}
