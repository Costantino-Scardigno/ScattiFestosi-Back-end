package com.gestioneFoto.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;

@Data
public class PhotoRequest {

    @NotBlank(message = "Photo URL is required")
    private String url;

    // Il timestamp può essere generato lato client o lato server
    @NotNull(message = "Timestamp is required")
    private Date timestamp;

    // Questi campi sono utili per associare la foto all'evento e all'utente
    @NotNull(message = "Event ID is required")
    private Long eventId;

    @NotNull(message = "User ID is required")
    private Long userId;
}
