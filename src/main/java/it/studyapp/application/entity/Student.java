package it.studyapp.application.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.vaadin.flow.component.html.Image;

import it.studyapp.application.security.CustomUserDetails;
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
	private String firstName;
	
	@NotBlank
	private String lastName;
	
	@NotBlank
	private String password;
	
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
	private List<String> roles;
	
	@ManyToMany(mappedBy = "participants", fetch = FetchType.EAGER)
	@NotNull
	private List<Session> sessions = new ArrayList<>();
	
	@ManyToMany(mappedBy = "members", fetch = FetchType.EAGER)
	@NotNull
	private List<StudentGroup> studentGroups = new ArrayList<>();
	
	@ManyToMany(mappedBy = "participants", fetch = FetchType.EAGER)
	@NotNull
	private List<CalendarEntryEntity> calendarEntries = new ArrayList<>();
	
	@OneToMany(mappedBy = "student", fetch = FetchType.EAGER)
	@NotNull
    private List<NotificationEntity> notifications = new ArrayList<>();
	
	public Student() {
		this.username = "unspecified";
		this.firstName = "unspecified";
		this.lastName = "unspecified";
		this.fieldOfStudy = null;
		this.birthDate = LocalDate.now();
		this.yearFollowing = null;
		this.password = "unspecified";
		this.avatar = 0;
		this.roles = Arrays.asList("ROLE_USER");
	}

	public Student(@NotBlank String username, @NotBlank String firstName, @NotBlank String lastName,
			@NotBlank String password, @NotNull LocalDate birthDate, String fieldOfStudy, String yearFollowing,
			@NotBlank @Email String email, @NotBlank int avatar, @NotNull List<String> roles) {
		this.username = username;
		this.firstName = firstName;
		this.lastName = lastName;
		this.password = password;
		this.birthDate = birthDate;
		this.fieldOfStudy = fieldOfStudy;
		this.yearFollowing = yearFollowing;
		this.email = email;
		this.avatar = avatar;
		this.roles = roles;
	}	

	public Student(CustomUserDetails user) {
		this.username = user.getUsername();
		this.firstName = user.getFirstName();
		this.lastName = user.getLastName();
		this.fieldOfStudy = user.getFieldOfStudy();
		this.yearFollowing = user.getYearFollowing();
		this.email = user.getEmail();
		this.avatar = user.getAvatar();
		this.password = user.getPassword();
		this.birthDate = user.getBirthDate();
		this.roles = new ArrayList<String>();
		user.getAuthorities().forEach(authority -> this.roles.add(authority.getAuthority()));
	}
	
	
	/* Utility methods */
	
	public String getIconUrl() {
		Image picture = new Image("images/user" + (avatar+1) + ".jpg", "User icon");
		return picture.getSrc();
	}
	

	/* Getters and Setters */

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public List<String> getRoles() {
		return roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
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
	
	public List<CalendarEntryEntity> getCalendarEntries() {
		return calendarEntries;
	}

	public void setCalendarEntries(List<CalendarEntryEntity> calendarEntries) {
		this.calendarEntries = calendarEntries;
	}

	public List<NotificationEntity> getNotifications() {
		return notifications;
	}

	public void setNotifications(List<NotificationEntity> notifications) {
		this.notifications = notifications;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
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
