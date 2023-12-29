package it.studyapp.application.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
public class StudentGroup extends AbstractEntity {

	@NotBlank
	private String name;
	
	@ManyToOne
	@NotNull
	private Student owner;
	
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "studentgroup_student",
		joinColumns = @JoinColumn(name = "studentgroup_id"),
		inverseJoinColumns = @JoinColumn(name = "student_id"))
	@NotNull
	private List<Student> members = new ArrayList<>();
	
	public StudentGroup() {
		this.name = "unspecified";
		this.owner = null;
	}

	public StudentGroup(@NotBlank String name, @NotNull Student owner, @NotNull List<Student> members) {
		this.name = name;
		this.owner = owner;
		this.members = members;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Student getOwner() {
		return owner;
	}

	public void setOwner(Student owner) {
		this.owner = owner;
	}

	public List<Student> getMembers() {
		return members;
	}

	public void setMembers(List<Student> members) {
		this.members = members;
	}
	
	public void addMember(Student member) {
		if(!this.members.contains(member))
			this.members.add(member);
	}
	
	public void addMembers(Collection<? extends Student> c) {
		for(Student s : c) {
			if(!this.members.contains(s))
				this.members.add(s);
		}
	}
	
	public void removeMember(Student member) {
		this.members.remove(member);
	}
	
	public void removeAllMembers() {
		this.members.clear();
	}
	
	@Override
	public String toString() {
		return String.format("Name: %s, Owner: %s, #Members: %d", this.name, this.owner.getUsername(), this.members.size());
	}
}
