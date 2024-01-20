package it.studyapp.application.security.keycloak;

import it.studyapp.application.security.config.KeycloakConfig;

import it.studyapp.application.entity.Student;
import it.studyapp.application.service.UserService;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
class KeycloakUserService implements UserService {
	
	private KeycloakConfig keycloakConfig;
	private final String appBaseUrl;
	private final String clientId;
	
	public KeycloakUserService(KeycloakConfig keycloakConfig,
							   @Value("${studyapp.base-url}") String appBaseUrl,
							   @Value("${spring.security.oauth2.client.registration.keycloak.client-id}") String clientId) {
		
		this.keycloakConfig = keycloakConfig;
		this.appBaseUrl = appBaseUrl;
		this.clientId = clientId;
	}

    @Override
	public void updateUser(Student student) {
	    UserRepresentation user = new UserRepresentation();
	    user.setUsername(student.getUsername());
	    user.setFirstName(student.getFirstName());
	    user.setLastName(student.getLastName());
	    user.setEmail(student.getEmail());
	    user.singleAttribute("birthDate", student.getBirthDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
	    user.singleAttribute("yearFollowing", student.getYearFollowing());
	    user.singleAttribute("fieldOfStudy", student.getFieldOfStudy());
	    
	    UsersResource usersResource = getInstance();
	    usersResource.get(student.getKeycloakId()).update(user);
	}

	@Override
	public void resetPassword(String userId) {
        UsersResource usersResource = getInstance();

        usersResource.get(userId)
                .executeActionsEmail(clientId, appBaseUrl, 600, List.of("UPDATE_PASSWORD"));
	}

    private UsersResource getInstance(){
        return keycloakConfig.getInstance().realm(keycloakConfig.getRealm()).users();
    }

}