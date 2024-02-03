package it.studyapp.application.entity;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;

import com.vaadin.flow.component.html.Image;

import it.studyapp.application.security.Roles;
import it.studyapp.application.service.UserInfo;
import jakarta.annotation.Nullable;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
public class Student extends AbstractEntity {
	
	@NotBlank
	private String username;
	
	@NotBlank
	private String keycloakId;
	
	@NotBlank
	private String firstName;
	
	@NotBlank
	private String lastName;
	
	@NotNull
	private LocalDate birthDate;
	
	@Nullable
	private String fieldOfStudy;
	
	@Nullable
	private String yearFollowing;
	
	@NotBlank
	@Email
	private String email;

	private int avatar;
	
	@NotNull
	private List<String> roles = new ArrayList<>();
	
	@ManyToMany(mappedBy = "participants", fetch = FetchType.EAGER)
	@NotNull
	private List<Session> sessions = new ArrayList<>();
	
	@ManyToMany(mappedBy = "members", fetch = FetchType.EAGER)
	@NotNull
	private List<StudentGroup> studentGroups = new ArrayList<>();
	
	@OneToMany(mappedBy = "student", fetch = FetchType.EAGER)
	@NotNull
    private List<NotificationEntity> notifications = new ArrayList<>();
	
	public Student() {
		this.username = "unspecified";
		this.keycloakId = "unspecified";
		this.firstName = "unspecified";
		this.lastName = "unspecified";
		this.fieldOfStudy = null;
		this.birthDate = LocalDate.now();
		this.yearFollowing = null;
		this.avatar = 0;
		this.roles.add(Roles.USER);
	}

	public Student(@NotBlank String username, @NotBlank String keycloakId, @NotBlank String firstName,
			@NotBlank String lastName, @NotNull LocalDate birthDate, String fieldOfStudy,
			String yearFollowing, @NotBlank @Email String email, @NotBlank int avatar) {
		this.username = username;
		this.keycloakId = keycloakId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.birthDate = birthDate;
		this.fieldOfStudy = fieldOfStudy;
		this.yearFollowing = yearFollowing;
		this.email = email;
		this.avatar = avatar;
		this.roles.add(Roles.USER);
	}	

	public Student(UserInfo user) {
		this.username = user.getUsername();
		this.keycloakId = user.getId();
		this.firstName = user.getFirstName();
		this.lastName = user.getLastName();
		this.email = user.getEmail();
		this.avatar = 0;
		this.birthDate = LocalDate.parse(user.getBirthDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		this.fieldOfStudy = user.getFieldOfStudy();
		this.yearFollowing = user.getYearFollowing();
		
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            this.roles = authentication.getAuthorities().stream().map(auth -> auth.getAuthority()).toList();
        } else
        	this.roles.add(Roles.USER);
	}
	
	
	/* Utility methods */
	
	public String getIconUrl() {
		Image picture = new Image("images/user" + (avatar+1) + ".jpg", "User icon");
		return picture.getSrc();
	}
	

	/* Getters and Setters */
	
	public String getKeycloakId() {
		return keycloakId;
	}
	
	public void setKeycloakId(String keycloakId) {
		this.keycloakId = keycloakId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public LocalDate getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(LocalDate birthDate) {
		this.birthDate = birthDate;
	}

	public String getYearFollowing() {
		return yearFollowing;
	}

	public void setYearFollowing(String yearFollowing) {
		this.yearFollowing = yearFollowing;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getAvatar() {
		return avatar;
	}

	public void setAvatar(int avatar) {
		this.avatar = avatar;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getFieldOfStudy() {
		return fieldOfStudy;
	}

	public void setFieldOfStudy(String fieldOfStudy) {
		this.fieldOfStudy = fieldOfStudy;
	}
	
	public List<String> getRoles() {
		return roles;
	}
	
	public void setRoles(List<String> roles) {
		this.roles = roles;
	}
	
	public boolean hasRole(String role) {
		return this.roles.contains(role);
	}

	public List<Session> getSessions() {
		return sessions;
	}

	public void setSessions(List<Session> sessions) {
		this.sessions = sessions;
	}

	public List<StudentGroup> getStudentGroups() {
		return studentGroups;
	}

	public void setStudentGroups(List<StudentGroup> studentGroups) {
		this.studentGroups = studentGroups;
	}

	public List<NotificationEntity> getNotifications() {
		return notifications;
	}

	public void setNotifications(List<NotificationEntity> notifications) {
		this.notifications = notifications;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		builder.append(String.format("Id: %d, Version: %d, Username: %s, First Name: %s, Last Name: %s, Field of Study: %s, #Sessions: %d, #Groups: %d", 
				this.getId(), this.getVersion(), this.username, this.firstName, this.lastName, this.fieldOfStudy, this.sessions.size(), this.studentGroups.size()));
		/*
		builder.append("\n    Sessions:\n");
		for(Session s : this.sessions)
			builder.append("      " + s + "\n");
		
		builder.append("\n    Groups:\n");
		for(StudentGroup group : this.studentGroups)
			builder.append("      " + group + "\n");
		*/
		return builder.toString();
	}

}
