package com.gestioneFoto.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CommentRequest {

    @NotBlank(message = "Comment content is required")
    private String content;

    @NotNull(message = "Photo ID is required")
    private Long photoId;
}