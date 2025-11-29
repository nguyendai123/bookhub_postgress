package com.bookhup.repository;

import com.bookhup.model.UserFeedWeights;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserFeedWeightsRepository extends JpaRepository<UserFeedWeights, Long> {
}

