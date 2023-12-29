package it.studyapp.application.presenter.layout;

import it.studyapp.application.view.layout.MainLayout;

public interface MainLayoutPresenter {
	
	public void setView(MainLayout view);
	public void updateAvatar();
	public void createNotifications();
	public void registerUI();
	public void unregisterUI();
	public void logout();

}
