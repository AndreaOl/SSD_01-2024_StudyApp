package it.studyapp.application.entity;

import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;

@Entity
public class Token extends AbstractEntity {
	
	@NotBlank
	private String randomToken;
	
	@NotBlank
	private String email;
	
	public Token() {
		randomToken = "undefined";
		email = "undefined";
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Token(@NotBlank String randomToken, @NotBlank String email) {
		this.randomToken = randomToken;
		this.email = email;
	}

	public String getRandomToken() {
		return randomToken;
	}

	public void setRandomToken(String randomToken) {
		this.randomToken = randomToken;
	}


}
