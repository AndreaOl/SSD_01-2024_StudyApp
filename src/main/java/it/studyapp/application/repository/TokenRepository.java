package it.studyapp.application.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import it.studyapp.application.entity.Token;


public interface TokenRepository extends JpaRepository<Token, Long>{
	@Query("SELECT t FROM Token t " +
            "WHERE t.randomToken = :randomToken")
	List<Token> searchToken(@Param("randomToken") String randomToken);
	
	
	@Query("SELECT t FROM Token t " +
            "WHERE t.email = :email")
	List<Token> searchEmail(@Param("email") String email);
	
	@Transactional
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Token t " +
            "SET t.randomToken = :randomToken " +
            "WHERE t.email = :email")
    void update(@Param("randomToken") String randomToken,
                @Param("email") String email);
}

