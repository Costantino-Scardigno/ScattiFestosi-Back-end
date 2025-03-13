package com.gestioneFoto.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "photos")
public class Photo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // URL dove è memorizzata l'immagine (può essere un link a S3 o a un server locale)
    @Column(nullable = false)
    private String url;

    // Timestamp della foto
    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp;

    // Relazione con l'evento di cui fa parte la foto
    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    // Utente che ha caricato la foto
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Relazione one-to-many con i commenti
    @OneToMany(mappedBy = "photo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    // Relazione one-to-many con i like
    @OneToMany(mappedBy = "photo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Like> likes = new ArrayList<>();

    // Metodo di utilità per contare i like
    @Transient
    public int getLikeCount() {
        return likes != null ? likes.size() : 0;
    }
}