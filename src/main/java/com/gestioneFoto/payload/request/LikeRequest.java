package com.gestioneFoto.payload.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LikeRequest {

    @NotNull(message = "Photo ID is required")
    private Long photoId;
}
