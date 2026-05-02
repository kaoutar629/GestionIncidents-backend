package com.kaoutar.gestionIncidents.repository;

import com.kaoutar.gestionIncidents.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // Récupère les notifs de l'utilisateur, triées par date desc
    @Query("SELECT n FROM Notification n WHERE n.username = :username ORDER BY n.at DESC")
    List<Notification> findByUsername(@Param("username") String username);

    List<Notification> findByUsernameAndIsReadFalse(String username);
}
