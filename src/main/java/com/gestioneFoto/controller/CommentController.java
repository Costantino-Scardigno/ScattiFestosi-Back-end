package com.gestioneFoto.controller;

import com.gestioneFoto.model.Comment;
import com.gestioneFoto.model.User;
import com.gestioneFoto.payload.request.CommentRequest;
import com.gestioneFoto.payload.response.CommentResponse;
import com.gestioneFoto.payload.response.MessageResponse;
import com.gestioneFoto.service.CommentService;
import com.gestioneFoto.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;
    private final UserService userService;

    public CommentController(CommentService commentService, UserService userService) {
        this.commentService = commentService;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<?> addComment(@Valid @RequestBody CommentRequest commentRequest,
                                        Authentication authentication) {
        User user = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Comment comment = commentService.addComment(
                commentRequest.getContent(),
                commentRequest.getPhotoId(),
                user);

        CommentResponse commentResponse = commentService.convertToDto(comment);
        return ResponseEntity.ok(commentResponse);
    }

    @GetMapping("/photo/{photoId}")
    public ResponseEntity<?> getCommentsByPhotoId(@PathVariable Long photoId) {
        List<Comment> comments = commentService.getCommentsByPhotoId(photoId);
        List<CommentResponse> commentResponses = comments.stream()
                .map(commentService::convertToDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(commentResponses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateComment(@PathVariable Long id,
                                           @Valid @RequestBody CommentRequest commentRequest,
                                           Authentication authentication) {
        User user = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Comment updatedComment = commentService.updateComment(
                id,
                commentRequest.getContent(),
                user);

        CommentResponse commentResponse = commentService.convertToDto(updatedComment);
        return ResponseEntity.ok(commentResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteComment(@PathVariable Long id, Authentication authentication) {
        User user = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        commentService.deleteComment(id, user);
        return ResponseEntity.ok(new MessageResponse("Comment deleted successfully"));
    }
}