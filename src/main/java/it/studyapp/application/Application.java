package it.studyapp.application;

import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.server.AppShellSettings;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The entry point of the Spring Boot application.
 *
 * Use the @PWA annotation make the application installable on phones, tablets
 * and some desktop browsers.
 *
 */

@SpringBootApplication
@NpmPackage(value = "@fontsource/open-sans", version = "4.5.0")
@Theme(value = "studyapp")
@PWA(
        name = "StudyApp",
        shortName = "SA",
        offlinePath="offline.html",
        offlineResources = { "images/offline.png" }
)
@Push
public class Application implements AppShellConfigurator {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Map<String, UI> activeUsers = new ConcurrentHashMap<>();
	
	private static final Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
    
    @Override
	public void configurePage(AppShellSettings settings) {
    	settings.addFavIcon("icon", "icons/icon.png", "512x512");
		AppShellConfigurator.super.configurePage(settings);
	}

    public static void register(String username, UI ui) {
    	if(username.equals("admin"))
    		logger.warn("Admin logged in");
        if(activeUsers.put(username, ui) == null)
        	logger.info(username + " added to active users. UI: " + ui);
    }

    public static void unregister(String username) {
    	if(username.equals("admin"))
    		logger.warn("Admin logged out");
    	UI ui = activeUsers.remove(username);
        if(ui != null)
        	logger.info(username + " removed from active users. UI: " + ui);
    }
    
    public static UI getUserUI(String username) {
    	return activeUsers.get(username);
    }
    
}
