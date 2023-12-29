package it.studyapp.application.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import it.studyapp.application.entity.SessionRequest;

public interface SessionRequestRepository extends JpaRepository<SessionRequest, Long> {
	
	@Query("SELECT sr FROM SessionRequest sr " +
            "WHERE sr.sessionId = :sessionId")
	List<SessionRequest> search(@Param("sessionId") Long sessionId);

}
