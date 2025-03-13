package com.gestioneFoto.repository;

import com.gestioneFoto.model.Comment;
import com.gestioneFoto.model.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPhotoOrderByCreatedAtDesc(Photo photo);
    List<Comment> findByPhotoIdOrderByCreatedAtDesc(Long photoId);
}