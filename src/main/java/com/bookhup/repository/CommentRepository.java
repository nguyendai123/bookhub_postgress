package com.bookhup.repository;

import com.bookhup.model.Comment;
import com.bookhup.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByPost_PostIDOrderByCreateAtDesc(long postID);


    void deleteByPost(Post post);
}
