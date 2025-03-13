package com.gestioneFoto.controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.gestioneFoto.model.Event;
import com.gestioneFoto.model.Photo;
import com.gestioneFoto.model.User;
import com.gestioneFoto.payload.response.MessageResponse;
import com.gestioneFoto.payload.response.PhotoResponse;
import com.gestioneFoto.service.EventService;
import com.gestioneFoto.service.PhotoService;
import com.gestioneFoto.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/photos")
public class PhotoController {

    private final PhotoService photoService;
    private final EventService eventService;
    private final UserService userService;
    private final Cloudinary cloudinary;

    public PhotoController(PhotoService photoService,
                           EventService eventService,
                           UserService userService,
                           Cloudinary cloudinary) {
        this.photoService = photoService;
        this.eventService = eventService;
        this.userService = userService;
        this.cloudinary = cloudinary;
    }

    /**
     * Endpoint per caricare una foto su Cloudinary.
     * Parametri:
     * - file: il file da caricare (MultipartFile)
     * - eventId: ID dell'evento a cui associare la foto
     * - userId: ID dell'utente che carica la foto
     */
    @PostMapping("/upload")
    public ResponseEntity<?> uploadPhotoFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("eventId") Long eventId,
            Authentication authentication) {
        try {
            // Carica il file su Cloudinary e ottieni l'URL sicuro
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            String imageUrl = (String) uploadResult.get("secure_url");

            // Recupera l'evento tramite l'ID
            Optional<Event> eventOptional = eventService.getEventById(eventId);
            if (!eventOptional.isPresent()) {
                return ResponseEntity.badRequest().body(new MessageResponse("Invalid event ID"));
            }
            Event event = eventOptional.get();

            // Recupera l'utente dal token di autenticazione
            String username = authentication.getName();
            User user = userService.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Crea un nuovo oggetto Photo e imposta i dati
            Photo photo = new Photo();
            photo.setUrl(imageUrl);
            photo.setTimestamp(new Date());
            photo.setEvent(event);
            photo.setUser(user);
            Photo savedPhoto = photoService.savePhoto(photo);

            PhotoResponse photodto = photoService.convertToDto(photo);
            return ResponseEntity.ok(photodto);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(new MessageResponse("File upload failed: " + ex.getMessage()));
        }
    }

    /**
     * Endpoint per recuperare tutte le foto associate a un evento.
     * URL: GET /api/photos/event/{eventId}
     */
    @GetMapping("/event/{eventId}")
    public ResponseEntity<?> getPhotosByEvent(@PathVariable Long eventId,
                                              @RequestParam(defaultValue = "false") boolean includeDetails) {
        List<Photo> photos = photoService.getPhotosByEventId(eventId);
        List<PhotoResponse> photoResponses;

        if (includeDetails) {
            photoResponses = photos.stream()
                    .map(photoService::convertToDtoWithDetails)
                    .collect(Collectors.toList());
        } else {
            photoResponses = photos.stream()
                    .map(photoService::convertToDto)
                    .collect(Collectors.toList());
        }

        if (photoResponses.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Questo evento non ha foto!!");
        }
        return ResponseEntity.ok(photoResponses);
    }

    /**
     * Endpoint per aggiornare una foto.
     * URL: PUT /api/photos/{id}
     * Il corpo della richiesta deve contenere i dettagli della foto (ad es., URL e timestamp).
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePhoto(@PathVariable Long id, @RequestBody Photo photoDetails) {
        photoDetails.setId(id);
        Photo updatedPhoto = photoService.updatePhoto(photoDetails);
        return ResponseEntity.ok(updatedPhoto);
    }

    /**
     * Endpoint per cancellare una foto.
     * URL: DELETE /api/photos/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePhoto(@PathVariable Long id) {
        photoService.deletePhoto(id);
        return ResponseEntity.ok(new MessageResponse("Photo deleted successfully."));
    }

    /**
     * Endpoint per ottenere una singola foto con tutti i dettagli (commenti e like).
     * URL: GET /api/photos/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getPhotoById(@PathVariable Long id,
                                          @RequestParam(defaultValue = "false") boolean includeDetails) {
        Optional<Photo> photoOptional = photoService.getPhotoById(id);
        if (!photoOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse("Photo not found"));
        }

        Photo photo = photoOptional.get();
        PhotoResponse photoResponse;

        if (includeDetails) {
            photoResponse = photoService.convertToDtoWithDetails(photo);
        } else {
            photoResponse = photoService.convertToDto(photo);
        }

        return ResponseEntity.ok(photoResponse);
    }
}