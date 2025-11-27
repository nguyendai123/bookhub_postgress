package com.bookhup.repository;

import com.bookhup.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByOrderByCreatedAtDesc();

    @Query("""
            SELECT p FROM Post p
                        WHERE p.lastHashtagScannedAt IS NULL
                        OR p.updatedAt > p.lastHashtagScannedAt
                       """)
    List<Post> findPostsNeedScan();

}
