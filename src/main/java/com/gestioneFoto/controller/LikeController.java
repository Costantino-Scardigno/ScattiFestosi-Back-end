package com.gestioneFoto.controller;

import com.gestioneFoto.model.Like;
import com.gestioneFoto.model.User;
import com.gestioneFoto.payload.request.LikeRequest;
import com.gestioneFoto.payload.response.LikeResponse;
import com.gestioneFoto.payload.response.MessageResponse;
import com.gestioneFoto.service.LikeService;
import com.gestioneFoto.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/likes")
public class LikeController {

    private final LikeService likeService;
    private final UserService userService;

    public LikeController(LikeService likeService, UserService userService) {
        this.likeService = likeService;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<?> addLike(@Valid @RequestBody LikeRequest likeRequest,
                                     Authentication authentication) {
        User user = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Like like = likeService.addLike(likeRequest.getPhotoId(), user);
        LikeResponse likeResponse = likeService.convertToDto(like);

        return ResponseEntity.ok(likeResponse);
    }

    @DeleteMapping("/photo/{photoId}")
    public ResponseEntity<?> removeLike(@PathVariable Long photoId,
                                        Authentication authentication) {
        User user = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        likeService.removeLike(photoId, user);
        return ResponseEntity.ok(new MessageResponse("Like removed successfully"));
    }

    @GetMapping("/photo/{photoId}")
    public ResponseEntity<?> getLikesByPhotoId(@PathVariable Long photoId) {
        List<Like> likes = likeService.getLikesByPhotoId(photoId);
        List<LikeResponse> likeResponses = likes.stream()
                .map(likeService::convertToDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(likeResponses);
    }

    @GetMapping("/photo/{photoId}/status")
    public ResponseEntity<?> checkLikeStatus(@PathVariable Long photoId,
                                             Authentication authentication) {
        User user = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean hasLiked = likeService.hasUserLiked(photoId, user.getId());
        return ResponseEntity.ok(new MessageResponse(String.valueOf(hasLiked)));
    }
}