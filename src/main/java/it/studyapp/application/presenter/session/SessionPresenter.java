package it.studyapp.application.presenter.session;

import it.studyapp.application.entity.Session;
import it.studyapp.application.entity.Student;
import it.studyapp.application.view.session.SessionView;

public interface SessionPresenter {
	
	public void setView(SessionView view);
	public void updateSessionGrid();
	public void createSession();
	public void leaveSession();
	public void onSessionClick(Session session);
	public void onSessionDoubleClick(Session session);
	public void onParticipantDoubleClick(Student clickedStudent);
	
}