package com.gestioneFoto.repository;

import com.gestioneFoto.model.Event;
import com.gestioneFoto.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByCreatedBy(User user);

    // Metodo per trovare un evento tramite il codice di condivisione
    Optional<Event> findByShareCode(String shareCode);

    // Metodo per trovare eventi condivisi con un utente specifico
    @Query("SELECT e FROM Event e WHERE :userId MEMBER OF e.sharedWithUserIds")
    List<Event> findBySharedWithUserIds(@Param("userId") Long userId);
}