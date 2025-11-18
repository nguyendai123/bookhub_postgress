package com.bookhup.service;

import com.bookhup.model.Like;
import com.bookhup.model.Post;
import com.bookhup.model.User;

import java.util.List;

public interface LikeService {

    List<Like> findAllByPostId(Long postId);

    Like save(Like like);

    void delete(Like like);

    Like getLikeById(Long likeId);

    Like getLikeByUserIdAndPostId(Post post, User user);

    void deleteLike(Long likeId);

    void deleteLikesByPostId(Long postId);
}
