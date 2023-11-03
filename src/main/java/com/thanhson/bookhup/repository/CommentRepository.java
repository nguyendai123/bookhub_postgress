package com.thanhson.bookhup.repository;

import com.thanhson.bookhup.model.Comment;
import com.thanhson.bookhup.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByPost_PostIDOrderByCreateAtDesc(long postID);


    void deleteByPost(Post post);
}
