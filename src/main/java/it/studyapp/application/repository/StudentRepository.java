package it.studyapp.application.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import it.studyapp.application.entity.Student;

public interface StudentRepository extends JpaRepository<Student, Long> {
	
	@Query("SELECT s FROM Student s " +
            "WHERE s.username = :username")
	List<Student> search(@Param("username") String username);
	
	@Query("SELECT s FROM Student s " +
            "WHERE s.email = :email")
	List<Student> searchEmail(@Param("email") String email);

	@Transactional
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Student s " +
            "SET s.firstName = :firstName, s.lastName = :lastName, s.fieldOfStudy = :fieldOfStudy, s.birthDate = :birthDate, "+
    		"s.yearFollowing = :yearFollowing, s.email = :email , s.avatar= :avatar " +
            "WHERE s.username = :username")
    void update(@Param("firstName") String firstName,
                @Param("lastName") String lastName,
                @Param("fieldOfStudy") String fieldOfStudy,
                @Param("birthDate") LocalDate birthDate,
                @Param("yearFollowing") String yearFollowing,
                @Param("email") String email,
    			@Param("username") String username,
    			@Param("avatar") int avatar);
}
