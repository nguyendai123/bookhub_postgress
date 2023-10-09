package com.thanhson.bookhup.repository;

import com.thanhson.bookhup.model.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    List<Like> findAllByPost_PostID(Long postID);
}
