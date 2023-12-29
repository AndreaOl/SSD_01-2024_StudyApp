package it.studyapp.application.repository;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import it.studyapp.application.entity.CalendarEntryEntity;

public interface CalendarEntryEntityRepository extends JpaRepository<CalendarEntryEntity, Long> {

    @Query("SELECT e FROM CalendarEntryEntity e " +
            "WHERE e.originalID = :originalID")
	List<CalendarEntryEntity> search(@Param("originalID") String originalID);
    
    
    @Transactional
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE CalendarEntryEntity e " +
    		"SET e.title = :title, e.description = :description, e.startDateTime = :startDateTime, e.endDateTime = :endDateTime, e.color = :color, " +
    		"e.allDay = :allDay, e.recurring = :recurring, e.recurringStart = :recurringStart, e.recurringEnd = :recurringEnd, " +
    		"e.recurringDaysOfWeek = :recurringDaysOfWeek WHERE e.originalID = :originalID")
    void update(@Param("originalID") String originalID,
    			@Param("title") String title,
    			@Param("description") String description,
    			@Param("startDateTime") LocalDateTime startDateTime,
    			@Param("endDateTime") LocalDateTime endDateTime,
    			@Param("color") String color,
    			@Param("allDay") Boolean allDay,
    			@Param("recurring") Boolean recurring,
    			@Param("recurringStart") LocalDateTime recurringStart,
    			@Param("recurringEnd") LocalDateTime recurringEnd,
    			@Param("recurringDaysOfWeek") Set<DayOfWeek> recurringDaysOfWeek);

}
