package it.studyapp.application.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import it.studyapp.application.entity.NotificationEntity;

public interface NotificationEntityRepository extends JpaRepository<NotificationEntity, Long> {

}
