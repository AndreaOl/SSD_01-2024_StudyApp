package it.studyapp.application;

import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.server.AppShellSettings;
import com.vaadin.flow.theme.Theme;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
@Push
public class Application implements AppShellConfigurator {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Map<String, UI> activeUsers = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
    
    @Override
	public void configurePage(AppShellSettings settings) {
    	settings.addFavIcon("icon", "icons/icon.png", "512x512");
		AppShellConfigurator.super.configurePage(settings);
	}

    public static void register(String username, UI ui) {
        if(activeUsers.put(username, ui) == null)
        	System.out.println(username + " added to active users. UI: " + ui);
    }

    public static void unregister(String username) {
    	UI ui = activeUsers.remove(username);
        if(ui != null)
        	System.out.println(username + " removed from active users. UI: " + ui);
    }
    
    public static UI getUserUI(String username) {
    	return activeUsers.get(username);
    }
    
}
