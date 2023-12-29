package it.studyapp.application.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import it.studyapp.application.entity.StudentGroup;

public interface StudentGroupRepository extends JpaRepository<StudentGroup, Long> {

}
