package com.bookhup.repository;

import com.bookhup.model.Follow;
import com.bookhup.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {

    boolean existsByUserAndFollowUser(User user, User followUser);

    Optional<Follow> findByUserAndFollowUser(User user, User followUser);

    long countByFollowUser(User user); // số follower

    long countByUser(User user); // số user đang follow

    List<Follow> findByFollowUser_UserId(Long followUserId); // followers

    List<Follow> findByUser_UserId(Long userId);             // following

    // số follower
    long countByFollowUser_UserId(Long targetUserId);

    // số user đang follow
    long countByUser_UserId(Long targetUserId);

    /**
     * Lấy danh sách userId của followers
     * ai đang follow userId này
     */
    @Query("""
                select f.user.userId
                from Follow f
                where f.followUser.userId = :userId
            """)
    Set<Long> findFollowerIds(@Param("userId") Long userId);
}
