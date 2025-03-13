package com.gestioneFoto.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Date;

@Data
public class EventRequest {

    @NotBlank(message = "Event name is required")
    private String name;

    private String description;

    // Assicurati che il formato della data sia gestito correttamente lato client/servlet
    private Date eventDate;
}
