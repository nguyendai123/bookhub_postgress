package com.bookhup.service;

import com.bookhup.dto.request.like.LikeRequest;
import com.bookhup.model.Like;
import com.bookhup.model.Post;
import com.bookhup.model.User;

import java.util.List;

public interface LikeService {
    String toggleLike(LikeRequest req, User user);
}
