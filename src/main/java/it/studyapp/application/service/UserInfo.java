package it.studyapp.application.service;

import java.security.Principal;
import java.util.Map;


/**
 * Interface providing information about a user.
 */
public interface UserInfo extends Principal {
	
	/**
     * Gets the user's id.
     */
	String getId();

    /**
     * Gets the user's username. Please note that this should not be used to identify the user; for that purpose
     * you should use {@link #getName()} instead (which may or may not be the username).
     */
    String getUsername();

    /**
     * Gets the user's first name.
     */
    String getFirstName();

    /**
     * Gets the user's last name.
     */
    String getLastName();
    
    /**
     * Gets the user's email.
     */
    String getEmail();
    
    /**
     * Gets the user's year of study.
     */
    String getYearFollowing();
    
    /**
     * Gets the user's date of birth.
     */
    String getBirthDate();
    
    /**
     * Gets the user's field of study.
     */
    String getFieldOfStudy();
    
    /**
     * Gets a map of all the user attributes.
     */
    Map<String, Object> getAttributes();

    /**
     * Gets the user's full name, which is a combination of the first and last name. If the user has no first nor
     * last name, the user's username is used instead.
     */
    default String getFullName() {
        var firstName = getFirstName();
        var lastName = getLastName();

        var sb = new StringBuilder();
        if (!firstName.isBlank()) {
            sb.append(firstName);
        }
        if (!lastName.isBlank()) {
            if (sb.length() > 0) {
                sb.append(" ");
            }
            sb.append(lastName);
        }
        if (sb.length() == 0) {
            sb.append(getUsername());
        }
        return sb.toString();
    }
    
}
