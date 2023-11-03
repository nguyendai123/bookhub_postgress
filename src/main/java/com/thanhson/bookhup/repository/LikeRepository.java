package com.thanhson.bookhup.repository;

import com.thanhson.bookhup.model.Like;
import com.thanhson.bookhup.model.Post;
import com.thanhson.bookhup.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    List<Like> findAllByPost_PostID(Long postID);



    Like findByUserAndPost(User user, Post post);

    @Modifying
    @Query("DELETE FROM Like l WHERE l.post.postID = :postId")
    void deleteByPostId(Long postId);
}
