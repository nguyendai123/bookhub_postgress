package com.bookhup.repository;

import com.bookhup.model.BookReview;
import com.bookhup.model.Comment;
import com.bookhup.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPost(Post post);

    List<Comment> findByReview(BookReview review);

    List<Comment> findByParentId(Long parentId);

    List<Comment> findByPostOrderByCreatedAtAsc(Post post);

    List<Comment> findByReviewOrderByCreatedAtAsc(BookReview review);

    // Lấy bình luận cha (level 1)
    List<Comment> findByPostAndParentIdIsNullOrderByCreatedAtAsc(Post post);

    List<Comment> findByReviewAndParentIdIsNullOrderByCreatedAtAsc(BookReview review);

    // Lấy reply theo parentId
    List<Comment> findByParentIdOrderByCreatedAtAsc(Long parentId);
}
