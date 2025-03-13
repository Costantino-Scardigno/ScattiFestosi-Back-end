package com.gestioneFoto.payload.response;

import lombok.Data;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class EventResponse {
    private Long id;
    private String name;
    private String description;
    private Date eventDate;
    private String createdByUsername;
    private Long createdById;
    private int photoCount;
    private List<PhotoResponse> photos;
    private int TotalCommentCount;
    private int totalLikeCount;
    private String shareCode; // Codice di condivisione dell'album
    private Set<Long> sharedWithUserIds = new HashSet<>(); // IDs degli utenti con cui è condiviso
}