package it.studyapp.application.security.keycloak;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import it.studyapp.application.service.CurrentSession;
import it.studyapp.application.service.UserInfo;

import java.time.ZoneId;
import java.util.Optional;
import java.util.Map;

import static java.util.Objects.requireNonNull;
import static java.util.Objects.requireNonNullElse;

/**
 * Implementation of {@link CurrentSession} that accesses the user info from the current
 * {@link OAuth2AuthenticationToken}.
 */
@Component
class KeycloakCurrentSession implements CurrentSession {

	@Override
    public Optional<UserInfo> getCurrentUser() {
        return getOauth2User().map(user -> new UserInfo() {
            @Override
			public String getId() {
				return requireNonNull(user.getAttribute("sub"));
			}

			@Override
            public String getUsername() {
                return requireNonNullElse(user.getAttribute("preferred_username"), "");
            }

            @Override
            public String getFirstName() {
                return requireNonNullElse(user.getAttribute("given_name"), "");
            }

            @Override
            public String getLastName() {
                return requireNonNullElse(user.getAttribute("family_name"), "");
            }

            @Override
            public String getName() {
                return requireNonNull(user.getName());
            }
            
            @Override
            public Map<String, Object> getAttributes() {
            	return user.getAttributes();
            }

			@Override
			public String getEmail() {
				return requireNonNullElse(user.getAttribute("email"), "");
			}

			@Override
			public String getYearFollowing() {
				return requireNonNullElse(user.getAttribute("yearFollowing"), "");
			}

			@Override
			public String getBirthDate() {
				return requireNonNullElse(user.getAttribute("birthDate"), "");
			}

			@Override
			public String getFieldOfStudy() {
				return requireNonNullElse(user.getAttribute("fieldOfStudy"), "");
			};
        });
    }

    private Optional<OAuth2User> getOauth2User() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof OAuth2AuthenticationToken) {
            return Optional.of(((OAuth2AuthenticationToken) authentication).getPrincipal());
        }
        return Optional.empty();
    }

    @Override
    public ZoneId getTimeZone() {
        return getOauth2User()
                .map(user -> user.<String>getAttribute("zoneinfo"))
                .map(ZoneId::of)
                .orElse(ZoneId.systemDefault());
    }

    @Override
    public boolean hasRole(String role) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            return authentication.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals(role));
        }
        return false;
    }
}
