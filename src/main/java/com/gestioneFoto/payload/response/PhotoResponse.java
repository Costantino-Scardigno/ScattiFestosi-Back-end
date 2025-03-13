package com.gestioneFoto.payload.response;

import com.gestioneFoto.model.Event;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class PhotoResponse {
    private Long id;
    private String url;
    private Date timestamp;
    private Long eventId;
    private Long userId;
    private String username;
    private int likeCount;
    private int commentCount;
    private List<?> comments;
    private List<LikeResponse> likes;
    private EventResponse event;
}