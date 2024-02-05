package it.studyapp.application.security.config;

import com.vaadin.flow.spring.security.VaadinSavedRequestAwareAuthenticationSuccessHandler;
import com.vaadin.flow.spring.security.VaadinWebSecurity;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import it.studyapp.application.security.HttpSessionRepository;
import it.studyapp.application.security.HttpSessionRepositoryListener;
import it.studyapp.application.security.vaadin.VaadinAwareSecurityContextHolderStrategy;

/**
 * This class sets up Spring Security to protect our application.
 */
@Configuration
@EnableWebSecurity
class SecurityConfig extends VaadinWebSecurity {

    final ClientRegistrationRepository clientRegistrationRepository;
    final GrantedAuthoritiesMapper authoritiesMapper;
    
    final String baseUrl;

    SecurityConfig(ClientRegistrationRepository clientRegistrationRepository,
                   GrantedAuthoritiesMapper authoritiesMapper,
                   @Value("${studyapp.base-url}") String baseUrl) {
        this.clientRegistrationRepository = clientRegistrationRepository;
        this.authoritiesMapper = authoritiesMapper;
        SecurityContextHolder.setStrategyName(VaadinAwareSecurityContextHolderStrategy.class.getName());
        
        this.baseUrl = baseUrl;
    }

    @Bean
    public HttpSessionRepository httpSessionRepository() {
        return new HttpSessionRepository();
    }

    @Bean
    public ServletListenerRegistrationBean<HttpSessionRepositoryListener> sessionRepositoryListener() {
        var bean = new ServletListenerRegistrationBean<HttpSessionRepositoryListener>();
        bean.setListener(new HttpSessionRepositoryListener(httpSessionRepository()));
        return bean;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                // Enable OAuth2 login
                .oauth2Login(oauth2Login ->
                        oauth2Login
                                .clientRegistrationRepository(clientRegistrationRepository)
                                .userInfoEndpoint(userInfoEndpoint ->
                                        userInfoEndpoint
                                                // Use a custom authorities mapper to get the roles from the identity provider into the Authentication token
                                                .userAuthoritiesMapper(authoritiesMapper)
                                )
                                // Use a Vaadin aware authentication success handler
                                .successHandler(new VaadinSavedRequestAwareAuthenticationSuccessHandler())
                )
                // Configure logout
                .logout(logout ->
                        logout
                                // Enable OIDC logout (requires that we use the 'openid' scope when authenticating)
                                .logoutSuccessHandler(logoutSuccessHandler())
                                // When CSRF is enabled, the logout URL normally requires a POST request with the CSRF
                                // token attached. This makes it difficult to perform a logout from within a Vaadin
                                // application (since Vaadin uses its own CSRF tokens). By changing the logout endpoint
                                // to accept GET requests, we can redirect to the logout URL from within Vaadin.
                                .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET"))
                )
                // Configure access to static html pages and back-channel logout endpoint
                .authorizeHttpRequests(authorize ->
                		authorize.requestMatchers(new AntPathRequestMatcher("/logged-out.html"),
        					new AntPathRequestMatcher("/session-expired.html"),
        					new AntPathRequestMatcher("/back-channel-logout"))
                		.permitAll()
                );
        
        super.configure(http);
    }

    private OidcClientInitiatedLogoutSuccessHandler logoutSuccessHandler() {
        var logoutSuccessHandler = new OidcClientInitiatedLogoutSuccessHandler(clientRegistrationRepository);
        logoutSuccessHandler.setPostLogoutRedirectUri(baseUrl + "logged-out.html");
        return logoutSuccessHandler;
    }

}
