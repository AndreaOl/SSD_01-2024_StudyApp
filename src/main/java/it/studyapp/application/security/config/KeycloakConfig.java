package it.studyapp.application.security.config;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class KeycloakConfig {
	
    private Keycloak keycloak = null;
    private final String serverUrl;
    private final String realm;
    private final String clientId;
    private final String clientSecret;

    public KeycloakConfig(@Value("${keycloak-rest.server-url}") String serverUrl,
    					  @Value("${keycloak-rest.realm}") String realm,
    					  @Value("${keycloak-rest.client-id}") String clientId,
    					  @Value("${keycloak-rest.client-secret}") String clientSecret) {
    	
    	this.serverUrl = serverUrl;
    	this.realm = realm;
    	this.clientId = clientId;
    	this.clientSecret = clientSecret;
    }

    public Keycloak getInstance(){
        if(keycloak == null){

            keycloak = KeycloakBuilder.builder()
                    .serverUrl(serverUrl)
                    .realm(realm)
                    .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                    .clientId(clientId)
                    .clientSecret(clientSecret)
                    .resteasyClient(new ResteasyClientBuilder()
                            			.connectionPoolSize(10)
                            			.build()
                                   )
                    .build();
        }
        return keycloak;
    }
    
    public String getRealm() {
    	return realm;
    }
    
}