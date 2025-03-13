package com.gestioneFoto.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 500)
    private String description;

    // Data e ora dell'evento
    @Temporal(TemporalType.TIMESTAMP)
    private Date eventDate;

    // L'utente che ha creato l'evento (opzionale)
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User createdBy;

    // Relazione uno-a-molti con le foto caricate per l'evento
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Photo> photos;

    // Codice univoco per la condivisione pubblica dell'album
    @Column(name = "share_code", unique = true)
    private String shareCode;

    // IDs degli utenti con cui l'album è condiviso
    @ElementCollection
    @CollectionTable(name = "event_shared_users",
            joinColumns = @JoinColumn(name = "event_id"))
    @Column(name = "user_id")
    private Set<Long> sharedWithUserIds = new HashSet<>();
}