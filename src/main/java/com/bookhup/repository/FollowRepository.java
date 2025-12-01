package com.bookhup.repository;

import com.bookhup.model.Follow;
import com.bookhup.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {

    boolean existsByUserAndFollowUser(User user, User followUser);

    Optional<Follow> findByUserAndFollowUser(User user, User followUser);

    long countByFollowUser(User user); // số follower

    long countByUser(User user); // số user đang follow

    // Lấy danh sách follower (ai đang follow user này)
    List<Follow> findByFollowUser(User followUser);

    // Lấy danh sách following (user đang follow ai)
    List<Follow> findByUser(User user);
}
