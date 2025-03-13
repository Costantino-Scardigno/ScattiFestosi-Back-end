package com.gestioneFoto.service;

import com.gestioneFoto.model.Like;
import com.gestioneFoto.model.Photo;
import com.gestioneFoto.model.User;
import com.gestioneFoto.payload.response.LikeResponse;
import com.gestioneFoto.repository.LikeRepository;
import com.gestioneFoto.repository.PhotoRepository;
import com.gestioneFoto.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LikeService {

    private final LikeRepository likeRepository;
    private final PhotoRepository photoRepository;
    private final UserRepository userRepository;

    public LikeService(LikeRepository likeRepository,
                       PhotoRepository photoRepository,
                       UserRepository userRepository) {
        this.likeRepository = likeRepository;
        this.photoRepository = photoRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Like addLike(Long photoId, User user) {
        Photo photo = photoRepository.findById(photoId)
                .orElseThrow(() -> new RuntimeException("Photo not found"));

        // Verifica se l'utente ha già messo like alla foto
        Optional<Like> existingLike = likeRepository.findByPhotoAndUser(photo, user);
        if (existingLike.isPresent()) {
            return existingLike.get();
        }

        Like like = new Like();
        like.setPhoto(photo);
        like.setUser(user);
        like.setCreatedAt(new Date());

        return likeRepository.save(like);
    }

    @Transactional
    public void removeLike(Long photoId, User user) {
        Photo photo = photoRepository.findById(photoId)
                .orElseThrow(() -> new RuntimeException("Photo not found"));

        likeRepository.findByPhotoAndUser(photo, user)
                .ifPresent(likeRepository::delete);
    }

    public boolean hasUserLiked(Long photoId, Long userId) {
        return likeRepository.existsByPhotoIdAndUserId(photoId, userId);
    }

    public List<Like> getLikesByPhotoId(Long photoId) {
        return likeRepository.findByPhotoId(photoId);
    }

    public LikeResponse convertToDto(Like like) {
        LikeResponse dto = new LikeResponse();
        dto.setId(like.getId());
        dto.setCreatedAt(like.getCreatedAt());
        dto.setPhotoId(like.getPhoto().getId());
        dto.setUserId(like.getUser().getId());
        dto.setUsername(like.getUser().getUsername());
        return dto;
    }
}