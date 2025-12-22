package com.bookhup.cache;


import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.util.concurrent.TimeUnit;

public class OwnerCache {

    // postId -> ownerId
    public static final Cache<Long, Long> POST_OWNER_CACHE =
            Caffeine.newBuilder()
                    .maximumSize(1_000_000)
                    .expireAfterWrite(1, TimeUnit.HOURS)
                    .recordStats()
                    .build();

    // commentId -> ownerId
    public static final Cache<Long, Long> COMMENT_OWNER_CACHE =
            Caffeine.newBuilder()
                    .maximumSize(1_000_000)
                    .expireAfterWrite(1, TimeUnit.HOURS)
                    .build();

    // reviewId -> ownerId
    public static final Cache<Long, Long> REVIEW_OWNER_CACHE =
            Caffeine.newBuilder()
                    .maximumSize(1_000_000)
                    .expireAfterWrite(1, TimeUnit.HOURS)
                    .build();
}

