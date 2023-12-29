package it.studyapp.application.security;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import it.studyapp.application.service.DataService;
import it.studyapp.application.util.security.AuthorityComparator;
import it.studyapp.application.view.authentication.LoginView;

import com.vaadin.flow.spring.security.VaadinWebSecurity;

@EnableWebSecurity
@Configuration
public class SecurityConfig extends VaadinWebSecurity {	

    private PersistentUserDetailsManager manager;
  
    @Autowired
    private DataService dataService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth ->
                auth.requestMatchers(
                    AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/images/*.png"),  
        			AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/images/*.jpg"),
        			AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/audio/*.mp3"),
        			AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/icons/*.png"),
        			AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/audio/*.wav"),
        			AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/h2-console/**")).permitAll());
        super.configure(http);
        setLoginView(http, LoginView.class);
    }

    @Bean
    public UserDetailsService users() {
  
        CustomUserDetails user = new CustomUserDetails("user", "Francesco", "Luongo", "Ingegneria Informatica",
        		LocalDate.now().minusYears(23), "1° Anno Magistrale", "francone@gmail.com", 2,
        		"{bcrypt}$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxO/BTk76klW");
  
        CustomUserDetails admin = new CustomUserDetails("admin", "Giorgio", "Antonelli", "Ingegneria Informatica",
        		LocalDate.now().minusYears(24), "2° Anno Magistrale", "joe@gmail.com", 0,
        		"{bcrypt}$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxO/BTk76klW");
  
        Set<GrantedAuthority> authorities = Set.of(new SimpleGrantedAuthority("ROLE_USER"), new SimpleGrantedAuthority("ROLE_ADMIN"));
        AuthorityComparator authComp = new AuthorityComparator();
        authorities = Collections.unmodifiableSet(authComp.sortAuthorities(authorities));
        admin.setAuthorities(authorities);
        manager = new PersistentUserDetailsManager(dataService, user, admin);
  
        return manager;
    }

    public PersistentUserDetailsManager getManager() {
    	return manager;
    }
}