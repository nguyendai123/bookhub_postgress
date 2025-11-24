package com.bookhup.service;

import com.bookhup.model.Post;
import com.bookhup.model.User;
import com.bookhup.request.post.PostRequest;

import java.util.List;

public interface PostService {

    Post createPost(PostRequest request, User user);

    List<Post> getAllPosts();

    Post updatePost(Long postId, Post post, User user);

    void deletePost(Long postId, User user);

    Post getPost(Long postId);
}
