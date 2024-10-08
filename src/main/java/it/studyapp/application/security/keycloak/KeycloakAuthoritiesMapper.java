package it.studyapp.application.security.keycloak;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Our Keycloak instance has been configured to expose the client roles inside the ID token under the claim
 * <code>resource_access.${client_id}.roles</code>. This mapper will fetch the roles from that claim and convert
 * them into <code>ROLE_</code> {@link GrantedAuthority authorities} that can be used directly by Spring Security.
 */
@Component
class KeycloakAuthoritiesMapper implements GrantedAuthoritiesMapper {

    private final String clientId;
    
    private final Logger logger = LoggerFactory.getLogger(KeycloakAuthoritiesMapper.class);

    KeycloakAuthoritiesMapper(@Value("${spring.security.oauth2.client.registration.keycloak.client-id}") String clientId) {
        this.clientId = clientId;
    }

    @SuppressWarnings("unchecked")
	@Override
    public Collection<? extends GrantedAuthority> mapAuthorities(Collection<? extends GrantedAuthority> authorities) {
        Set<GrantedAuthority> mappedAuthorities = new HashSet<>();
        var authority = authorities.iterator().next();
        
        if (authority instanceof OidcUserAuthority oidcUserAuthority) {
            OidcUserInfo userInfo = oidcUserAuthority.getUserInfo();
            if (userInfo.hasClaim("resource_access")) {
                var resourceAccess = userInfo.getClaimAsMap("resource_access");
                var roles = (Collection<String>) ((Map<String, Object>) resourceAccess.get(clientId)).get("roles");
                mappedAuthorities.addAll(roles.stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                        .toList());
            } else
            	logger.warn("User " + userInfo.getPreferredUsername() + " has no resource_access claim");
        } else
        	logger.warn("Authority is not an instance of OidcUserAuthority");
        
        logger.info("Mapped authorities: " + mappedAuthorities);
        
        return mappedAuthorities;
    }
}
