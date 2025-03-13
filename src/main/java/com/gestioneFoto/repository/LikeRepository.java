package com.gestioneFoto.repository;

import com.gestioneFoto.model.Like;
import com.gestioneFoto.model.Photo;
import com.gestioneFoto.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    List<Like> findByPhotoOrderByCreatedAtDesc(Photo photo);
    List<Like> findByPhotoId(Long photoId);
    Optional<Like> findByPhotoAndUser(Photo photo, User user);
    boolean existsByPhotoIdAndUserId(Long photoId, Long userId);
    void deleteByPhotoIdAndUserId(Long photoId, Long userId);
}