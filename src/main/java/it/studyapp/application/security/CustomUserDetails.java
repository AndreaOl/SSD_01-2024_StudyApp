package it.studyapp.application.security;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import it.studyapp.application.entity.Student;
import it.studyapp.application.util.security.AuthorityComparator;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CustomUserDetails implements UserDetails {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@NotBlank
	private String username;

	@NotBlank
	private String firstName;

	@NotBlank
	private String lastName;

	@NotBlank
	private String fieldOfStudy;

	@NotNull
	private LocalDate birthDate;

	@Nullable
	private String yearFollowing;

	@NotBlank
	@Email
	private String email;
	
	private int avatar;

	// FIXME Passwords should never be stored in plain text!
	@Size(min = 8, max = 68, message = "Password must be 8-64 char long")
	private String password;
	
	private Collection<? extends GrantedAuthority> authorities;

	public CustomUserDetails() {
		this.username = "unspecified";
		this.firstName = "unspecified";
		this.lastName = "unspecified";
		this.fieldOfStudy = "unspecified";
		this.birthDate = LocalDate.now();
		this.yearFollowing = "unspecified";
		this.email = "unspecified@not.damn";
		this.avatar = 0;
		this.password = "unspecified";
		this.authorities= Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));
	}
	
	public CustomUserDetails(@NotBlank String username, @NotBlank String firstName, @NotBlank String lastName,
			@NotBlank String fieldOfStudy, @NotNull LocalDate birthDate, String yearFollowing,
			@NotBlank @Email String email, @NotBlank int avatar,
			@Size(min = 8, max = 64, message = "Password must be 8-64 char long") String password) {
		this.username = username;
		this.firstName = firstName;
		this.lastName = lastName;
		this.fieldOfStudy = fieldOfStudy;
		this.birthDate = birthDate;
		this.yearFollowing = yearFollowing;
		this.email = email;
		this.avatar = avatar;
		this.password = password;
		this.authorities= Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));
	}

	public CustomUserDetails(Student student) {
		this.username = student.getUsername();
		this.firstName = student.getFirstName();
		this.lastName = student.getLastName();
		this.fieldOfStudy = student.getFieldOfStudy();
		this.birthDate = student.getBirthDate();
		this.yearFollowing = student.getYearFollowing();
		this.email = student.getEmail();
		this.avatar = student.getAvatar();
		this.password = student.getPassword();
		
        ArrayList<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        student.getRoles().forEach(role -> authorities.add(new SimpleGrantedAuthority(role)));
        AuthorityComparator authComp = new AuthorityComparator();
        this.authorities= Collections.unmodifiableSet(authComp.sortAuthorities(authorities));
	}


	//Setters

	public void setAuthorities(Collection<? extends GrantedAuthority> authorities) {
		this.authorities = authorities;
	}

	public void setUsername(String username) {this.username = username;}


	public void setFirstName(String firstName) {this.firstName = firstName;}

	public void setLastName(String lastName) {this.lastName = lastName;}

	public void setFieldOfStudy(String fieldOfStudy) {
		this.fieldOfStudy = fieldOfStudy;
	}
	public void setBirthDate(LocalDate birthDate) {this.birthDate = birthDate;}

	public void setYearFollowing(String yearFollowing) {this.yearFollowing = yearFollowing;}

	public void setEmail(String email) {this.email = email;}

	public void setAvatar(int avatar) {
		this.avatar = avatar;
	}

	public void setPassword(String password) {this.password = password;}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}
	
	//Getters
	

	public String getFieldOfStudy() {
		return fieldOfStudy;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public LocalDate getBirthDate() {
		return birthDate;
	}

	public String getYearFollowing() {
		return yearFollowing;
	}


	public String getEmail() {
		return email;
	}

	public int getAvatar() {
		return avatar;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
	
}

