package it.studyapp.application.entity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


import jakarta.annotation.Nullable;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinTable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
public class Session extends AbstractEntity {

	@Nullable
	private String subject;
	
	@NotNull
	private LocalDateTime date;
	
	@NotBlank
	private String location;
	
	@ManyToOne
	@NotNull
	private Student owner;
	
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "session_student",
    	joinColumns = @JoinColumn(name = "session_id"),
    	inverseJoinColumns = @JoinColumn(name = "student_id"))
	@NotNull
	private List<Student> participants = new ArrayList<>();
	
	public Session() {
		this.subject = null;
		this.date = LocalDateTime.now();
		this.location = "unspecified";
		this.owner = null;
	}
	
	public Session(String subject, @NotNull LocalDateTime date, @NotBlank String location, @NotNull Student owner, @NotNull List<Student> participants) {
		this.subject = subject;
		this.date = date;
		this.location = location;
		this.owner = owner;
		this.participants = participants;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public LocalDateTime getDate() {
		return date;
	}

	public void setDate(LocalDateTime date) {
		this.date = date;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public List<Student> getParticipants() {
		return participants;
	}

	public void setParticipants(List<Student> participants) {
		this.participants = participants;
	}

	public Student getOwner() {
		return owner;
	}

	public void setOwner(Student owner) {
		this.owner = owner;
	}
	
	public void addParticipant(Student participant) {
		if(!this.participants.contains(participant))
			this.participants.add(participant);
	}
	
	public void addParticipants(Collection<? extends Student> c) {
		for(Student s : c) {
			if(!this.participants.contains(s))
				this.participants.add(s);
		}
	}
	
	public void removeParticipant(Student participant) {
		this.participants.remove(participant);
	}
	
	public void removeAllParticipants() {
		this.participants.clear();
	}
	
	@Override
	public String toString() {
		return String.format("Id: %s, Subject: %s, Date: %s, Location: %s, Owner: %s, #Participants: %d", 
				this.getId(), this.subject, this.date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")), this.location, this.owner.getUsername(), this.participants.size());
	}
	
}
