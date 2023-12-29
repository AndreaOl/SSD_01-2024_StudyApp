package it.studyapp.application.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import it.studyapp.application.entity.StudentGroupRequest;

public interface StudentGroupRequestRepository extends JpaRepository<StudentGroupRequest, Long> {
	
	@Query("SELECT sgr FROM StudentGroupRequest sgr " +
            "WHERE sgr.studentGroupId = :studentGroupId")
	List<StudentGroupRequest> search(@Param("studentGroupId") Long studentGroupId);

}
