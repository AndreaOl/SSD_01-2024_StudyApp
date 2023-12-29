package it.studyapp.application.view.layout;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.server.StreamResource;

public interface MainLayout {
	
	public void setAvatarName(String username);
	public void setAvatarImage(StreamResource imageResource);
	public void setNumberOfNotifications(int count);
	public void addNotification(HorizontalLayout notification);
	public void removeNotification(HorizontalLayout notification);
	
}
