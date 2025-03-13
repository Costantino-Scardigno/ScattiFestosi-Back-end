package com.gestioneFoto.payload.response;

import lombok.Data;

import java.util.Date;

@Data
public class CommentResponse {
    private Long id;
    private String content;
    private Date createdAt;
    private Long photoId;
    private Long userId;
    private String username;
}