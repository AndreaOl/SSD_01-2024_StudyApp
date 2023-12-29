package it.studyapp.application.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import it.studyapp.application.entity.Reminder;

public interface ReminderRepository extends JpaRepository<Reminder, Long> {

}
