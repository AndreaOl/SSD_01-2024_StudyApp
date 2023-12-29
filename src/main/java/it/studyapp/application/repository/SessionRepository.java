package it.studyapp.application.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import it.studyapp.application.entity.Session;
import it.studyapp.application.entity.Student;

public interface SessionRepository extends JpaRepository<Session, Long> {
	
	@Transactional
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Session s " +
            "SET s.subject = :subject, s.date = :date, s.location = :location, s.owner = :owner " +
            "WHERE s.id = :id")
    void update(@Param("subject") String subject,
                @Param("date") LocalDateTime date,
                @Param("location") String location,
                @Param("owner") Student owner,
    			@Param("id") Long id);

}
