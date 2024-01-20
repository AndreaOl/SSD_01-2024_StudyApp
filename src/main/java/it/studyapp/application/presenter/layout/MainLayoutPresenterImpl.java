package it.studyapp.application.presenter.layout;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.server.StreamResource;

import it.studyapp.application.Application;
import it.studyapp.application.entity.NotificationEntity;
import it.studyapp.application.entity.Reminder;
import it.studyapp.application.entity.SessionRequest;
import it.studyapp.application.entity.Student;
import it.studyapp.application.entity.StudentGroupRequest;
import it.studyapp.application.event.NotificationCreatedEvent;
import it.studyapp.application.event.NotificationsReadEvent;
import it.studyapp.application.event.ProfileUpdatedEvent;
import it.studyapp.application.event.ReminderCreatedEvent;
import it.studyapp.application.event.SessionRequestCreatedEvent;
import it.studyapp.application.event.StudentCreatedEvent;
import it.studyapp.application.event.StudentGroupRequestCreatedEvent;
import it.studyapp.application.runnable.SessionRequestRunnable;
import it.studyapp.application.runnable.StudentGroupRequestRunnable;
import it.studyapp.application.security.SecurityService;
import it.studyapp.application.service.CalendarService;
import it.studyapp.application.service.DataService;
import it.studyapp.application.service.UserInfo;
import it.studyapp.application.view.layout.MainLayout;

@Component
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class MainLayoutPresenterImpl implements MainLayoutPresenter {
	
	private MainLayout view;
	
	private DataService dataService;
	private SecurityService securityService;
		
	private String authenticatedUser;
    private int notificationsCount = 0;
        
    public MainLayoutPresenterImpl(DataService dataService, SecurityService securityService, 
    		CalendarService calendarService) {
    	
        this.dataService = dataService;
        this.securityService = securityService;
        
        UserInfo currentUser = securityService.getAuthenticatedUser();
        if(dataService.searchStudent(currentUser.getUsername()).isEmpty()) {
        	Student newStudent = new Student(currentUser);
        	dataService.saveStudent(newStudent);
        	
        	UI adminUI = Application.getUserUI("admin");
        	if(adminUI != null)
        		adminUI.access(() -> ComponentUtil.fireEvent(adminUI, new StudentCreatedEvent(UI.getCurrent(), false)));
        }
            	        
        calendarService.checkEvents();
    }

	@Override
	public void setView(MainLayout view) {
		this.view = view;
	}

	@Override
	public void updateAvatar() {
		String username = securityService.getAuthenticatedUser().getUsername();
        StreamResource imageResource = new StreamResource("user.png",
                () -> getClass().getResourceAsStream("/META-INF/resources/images/user" + 
                		(dataService.searchStudent(username).get(0).getAvatar() + 1) + ".jpg"));
        view.setAvatarName(username);
        view.setAvatarImage(imageResource); 		
	}

	@Override
	public void createNotifications() {
		
		Student thisStudent = dataService.searchStudent(securityService.getAuthenticatedUser().getUsername()).get(0);
		notificationsCount = thisStudent.getNotifications().size();
		view.setNumberOfNotifications(notificationsCount);
		
	    thisStudent.getNotifications().forEach(n -> {        	
	    	final HorizontalLayout notification;
			
			/*----------Session Request----------*/
			
			if(n instanceof SessionRequest sr) {
				notification = sr.create(new SessionRequestRunnable(sr, dataService, securityService));
				
				final Long sr_id = sr.getId();
				Button closeBtn = new Button(VaadinIcon.CLOSE_SMALL.create(),
			    		clickEvent -> {
			    			view.removeNotification(notification);
			    			notificationsCount = Integer.max(0, notificationsCount - 1);
			    			view.setNumberOfNotifications(notificationsCount);
			    			
							SessionRequest persistentSR = dataService.findSessionRequestById(sr_id);
							if(persistentSR != null)
								dataService.deleteSessionRequest(persistentSR);
			    		});
			    closeBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
			    notification.add(closeBtn);
				
			/*----------Student Group Request----------*/
				
			} else if(n instanceof StudentGroupRequest sgr) {
				notification = sgr.create(new StudentGroupRequestRunnable(sgr, dataService, securityService));
				
				final Long sgr_id = sgr.getId();
				Button closeBtn = new Button(VaadinIcon.CLOSE_SMALL.create(),
			    		clickEvent -> {
			    			view.removeNotification(notification);
			    			notificationsCount = Integer.max(0, notificationsCount - 1);
			    			view.setNumberOfNotifications(notificationsCount);
			    			
							StudentGroupRequest persistentSGR = dataService.findStudentGroupRequestById(sgr_id);
							if(persistentSGR != null)
								dataService.deleteStudentGroupRequest(persistentSGR);
			    		});
			    closeBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
			    notification.add(closeBtn);
				
			/*----------Reminder----------*/
				
			} else if(n instanceof Reminder r) {				
				notification = r.create();
				
				final Long r_id = r.getId();
				Button closeBtn = new Button(VaadinIcon.CLOSE_SMALL.create(),
			    		clickEvent -> {
			    			view.removeNotification(notification);
			    			notificationsCount = Integer.max(0, notificationsCount - 1);
			    			view.setNumberOfNotifications(notificationsCount);
			    			
			    			Reminder persistentR = dataService.findReminderById(r_id);
			    			if(persistentR != null)
			    				dataService.deleteReminder(persistentR);
			    		});
			    closeBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
			    notification.add(closeBtn);
			    
			/*----------Info Notification----------*/
			    
			} else {
				notification = n.create();
				
				final Long n_id = n.getId();
				Button closeBtn = new Button(VaadinIcon.CLOSE_SMALL.create(),
			    		clickEvent -> {
			    			view.removeNotification(notification);
			    			notificationsCount = Integer.max(0, notificationsCount - 1);
			    			view.setNumberOfNotifications(notificationsCount);
			    			
			    			NotificationEntity persistentN = dataService.findNotificationById(n_id);
			    			if(persistentN != null)
			    				dataService.deleteNotification(persistentN);
			    		});
			    closeBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
			    notification.add(closeBtn);
			}
	
			view.addNotification(notification);
			
		});
	    
	    createListeners();
	    
	}
	
	@Override
	public void registerUI() {
		this.authenticatedUser = securityService.getAuthenticatedUser().getUsername();      
        Application.register(authenticatedUser, UI.getCurrent());
	}

	@Override
	public void unregisterUI() {
		Application.unregister(authenticatedUser);		
	}

	@Override
	public void logout() {
		securityService.logout();
	}

	@Override
	public boolean isAdmin() {
		return securityService.isAdmin();
	}

	private void createListeners() {
		
		/*----------Notifications Read----------*/
		
		ComponentUtil.addListener(UI.getCurrent(), NotificationsReadEvent.class, e -> {
			notificationsCount = 0;
			view.setNumberOfNotifications(notificationsCount);
		});
		
		/*----------Avatar Update----------*/
		
		ComponentUtil.addListener(UI.getCurrent(), ProfileUpdatedEvent.class, e -> {
	    	StreamResource imageResource = new StreamResource("user.png",
	                () -> getClass().getResourceAsStream("/META-INF/resources/images/user" + 
	                		(dataService.searchStudent(e.getUsername()).get(0).getAvatar() + 1) + ".jpg"));
	        view.setAvatarImage(imageResource);
	    });
		
		
		/*----------Session Request Created----------*/
		
	    ComponentUtil.addListener(UI.getCurrent(), SessionRequestCreatedEvent.class, e -> {
	    	++notificationsCount;
	    	view.setNumberOfNotifications(notificationsCount);
	    	
	    	SessionRequest sr = e.getSessionRequest();
	    	HorizontalLayout notification = sr.create(new SessionRequestRunnable(sr, dataService, securityService));
	    	
	    	final Long sr_id = sr.getId();
	    	Button closeBtn = new Button(VaadinIcon.CLOSE_SMALL.create(),
		    		clickEvent -> {
		    			notificationsCount = Integer.max(0, notificationsCount - 1);
		    	    	view.setNumberOfNotifications(notificationsCount);
		    			view.removeNotification(notification);
		    			
						SessionRequest persistentSR = dataService.findSessionRequestById(sr_id);
						if(persistentSR != null)
							dataService.deleteSessionRequest(persistentSR);
		    		});
		    closeBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
		    notification.add(closeBtn);
	
	    	view.addNotification(notification);
	    });
	    
	    
	    /*----------Student Group Request Created----------*/
	    
	    ComponentUtil.addListener(UI.getCurrent(), StudentGroupRequestCreatedEvent.class, e -> {
	    	++notificationsCount;
	    	view.setNumberOfNotifications(notificationsCount);
	    	
	    	StudentGroupRequest sgr = e.getStudentGroupRequest();
	    	HorizontalLayout notification = sgr.create(new StudentGroupRequestRunnable(sgr, dataService, securityService));
	    	
	    	final Long sgr_id = sgr.getId();
	    	Button closeBtn = new Button(VaadinIcon.CLOSE_SMALL.create(),
		    		clickEvent -> {
		    			notificationsCount = Integer.max(0, notificationsCount - 1);
		    	    	view.setNumberOfNotifications(notificationsCount);
		    	    	view.removeNotification(notification);
		    	    	
						StudentGroupRequest persistentSGR = dataService.findStudentGroupRequestById(sgr_id);
						if(persistentSGR != null)
							dataService.deleteStudentGroupRequest(persistentSGR);
		    		});
		    closeBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
		    notification.add(closeBtn);
	    	
	    	view.addNotification(notification);
	    });
	    
	    
	    /*----------Reminder Created----------*/
	    
	    ComponentUtil.addListener(UI.getCurrent(), ReminderCreatedEvent.class, e -> {
	    	++notificationsCount;
	    	view.setNumberOfNotifications(notificationsCount);
	    	
	    	Reminder r = e.getReminder();
	    	HorizontalLayout notification = r.create();
	    	
	    	final Long r_id = r.getId();
	    	Button closeBtn = new Button(VaadinIcon.CLOSE_SMALL.create(),
		    		clickEvent -> {
		    			notificationsCount = Integer.max(0, notificationsCount - 1);
		    	    	view.setNumberOfNotifications(notificationsCount);
		    			view.removeNotification(notification);
		    			
		    			Reminder persistentR = dataService.findReminderById(r_id);
		    			if(persistentR != null)
		    				dataService.deleteReminder(r);
		    		});
		    closeBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
		    notification.add(closeBtn);
		    
		    view.addNotification(notification);
	    });
	    
	    
	    /*----------Info Notification Created----------*/
	    
	    ComponentUtil.addListener(UI.getCurrent(), NotificationCreatedEvent.class, e -> {
	    	++notificationsCount;
	    	view.setNumberOfNotifications(notificationsCount);
	    	
	    	NotificationEntity n = e.getNotification();
	    	HorizontalLayout notification = n.create();
	    	
	    	final Long n_id = n.getId();
	    	Button closeBtn = new Button(VaadinIcon.CLOSE_SMALL.create(),
		    		clickEvent -> {
		    			notificationsCount = Integer.max(0, notificationsCount - 1);
		    	    	view.setNumberOfNotifications(notificationsCount);
		    			view.removeNotification(notification);
		    			
						NotificationEntity persistentN = dataService.findNotificationById(n_id);
						if(persistentN != null)
							dataService.deleteNotification(n);
		    		});
		    closeBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
		    notification.add(closeBtn);
		    
		    view.addNotification(notification);
	    });
		
	}

}
