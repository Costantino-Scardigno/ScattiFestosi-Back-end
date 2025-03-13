package com.gestioneFoto.service;

import com.gestioneFoto.model.Comment;
import com.gestioneFoto.model.Event;
import com.gestioneFoto.model.Photo;
import com.gestioneFoto.model.User;
import com.gestioneFoto.payload.response.CommentResponse;
import com.gestioneFoto.payload.response.EventResponse;
import com.gestioneFoto.payload.response.PhotoResponse;
import com.gestioneFoto.repository.EventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final PhotoService photoService;
    private final CommentService commentService;

    public EventService(EventRepository eventRepository, PhotoService photoService, CommentService commentService) {
        this.eventRepository = eventRepository;
        this.photoService = photoService;
        this.commentService = commentService;
    }

    public Event createEvent(Event event) {
        return eventRepository.save(event);
    }

    public List<Event> getEventsByUser(User user){
        return eventRepository.findByCreatedBy(user);
    }

    public List<Event> getSharedEventsByUser(User user) {
        return eventRepository.findBySharedWithUserIds(user.getId());
    }

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public Optional<Event> getEventById(Long id) {
        return eventRepository.findById(id);
    }

    // Metodo per cercare un evento tramite il codice di condivisione
    public Optional<Event> getEventByShareCode(String shareCode) {
        return eventRepository.findByShareCode(shareCode);
    }

    // Metodo per condividere un evento con un utente
    @Transactional
    public Event shareEventWithUser(Long eventId, Long userId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Evento non trovato"));

        event.getSharedWithUserIds().add(userId);
        return eventRepository.save(event);
    }

    // Metodo per rimuovere la condivisione con un utente
    @Transactional
    public Event removeEventShare(Long eventId, Long userId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Evento non trovato"));

        event.getSharedWithUserIds().remove(userId);
        return eventRepository.save(event);
    }

    @Transactional
    public Event updateEvent(Event event) {
        Event existingEvent = eventRepository.findById(event.getId())
                .orElseThrow(() -> new RuntimeException("Event not found"));

        existingEvent.setName(event.getName());
        existingEvent.setDescription(event.getDescription());
        existingEvent.setEventDate(event.getEventDate());
        // Manteniamo l'utente creatore originale

        // Se viene fornito un nuovo codice di condivisione, lo aggiorniamo
        if (event.getShareCode() != null) {
            existingEvent.setShareCode(event.getShareCode());
        }

        // Preserviamo la lista degli utenti con cui è condiviso
        if (event.getSharedWithUserIds() != null && !event.getSharedWithUserIds().isEmpty()) {
            existingEvent.setSharedWithUserIds(event.getSharedWithUserIds());
        }

        return eventRepository.save(existingEvent);
    }

    @Transactional
    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
    }

    public EventResponse convertEventToDto(Event event) {
        EventResponse dto = new EventResponse();
        dto.setId(event.getId());
        dto.setName(event.getName());
        dto.setDescription(event.getDescription());
        dto.setEventDate(event.getEventDate());
        dto.setCreatedByUsername(event.getCreatedBy() != null ? event.getCreatedBy().getUsername() : null);
        dto.setCreatedById(event.getCreatedBy() != null ? event.getCreatedBy().getId() : null);
        dto.setPhotoCount(event.getPhotos() != null ? event.getPhotos().size() : 0);

        // Aggiungiamo il codice di condivisione se presente
        if (event.getShareCode() != null && !event.getShareCode().isEmpty()) {
            dto.setShareCode(event.getShareCode());
        }

        // Aggiungiamo gli ID degli utenti con cui l'album è condiviso
        if (event.getSharedWithUserIds() != null) {
            dto.setSharedWithUserIds(event.getSharedWithUserIds());
        }

        // Calcola il numero totale di commenti
        int totalComments = 0;
        // Calcola il numero totale di like
        int totalLikes = 0;

        if (event.getPhotos() != null) {
            totalComments = event.getPhotos().stream()
                    .mapToInt(photo -> photo.getComments() != null ? photo.getComments().size() : 0)
                    .sum();

            totalLikes = event.getPhotos().stream()
                    .mapToInt(photo -> photo.getLikes() != null ? photo.getLikes().size() : 0)
                    .sum();
        }

        dto.setTotalCommentCount(totalComments);
        dto.setTotalLikeCount(totalLikes);

        return dto;
    }

    public EventResponse convertEventToDtoWithDetails(Event event) {
        EventResponse dto = convertEventToDto(event);

        // Aggiungi le foto con dettagli
        if (event.getPhotos() != null) {
            List<PhotoResponse> photoResponses = event.getPhotos().stream()
                    .map(photoService::convertToDtoWithDetails)
                    .collect(Collectors.toList());
            dto.setPhotos(photoResponses);
        }

        return dto;
    }
}