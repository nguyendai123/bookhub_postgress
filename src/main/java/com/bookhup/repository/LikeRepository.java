package com.bookhup.repository;

import com.bookhup.model.Like;
import com.bookhup.model.Post;
import com.bookhup.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    Like findByUserAndPost(User user, Post post);

    @Modifying
    @Query("DELETE FROM Like l WHERE l.post.postId = :postId")
    void deleteByPostId(Long postId);
}
