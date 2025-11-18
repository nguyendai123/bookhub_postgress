package com.bookhup.service;

import com.bookhup.model.Post;
import java.util.List;

public interface PostService {
    Post getPostById(Long postId);
    List<Post> findAll();
    List<Post> findAllByUser(long userId);
    long save(Post post);
    void delete(Post post);
}
