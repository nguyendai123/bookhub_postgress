package com.bookhup.service;

import com.bookhup.dto.response.post.PostFeedProjection;
import com.bookhup.model.Post;
import com.bookhup.model.User;
import com.bookhup.dto.request.post.PostRequest;
import org.springframework.data.domain.Page;

import java.util.List;

public interface PostService {

    Post createPost(PostRequest request, User user);

    Page<PostFeedProjection> getFeed(Long userId, int page, int size);

    List<Post> getAllPosts();

    Post updatePost(Long postId, PostRequest post, User user);

    void deletePost(Long postId, User user);

    Post getPost(Long postId);
}
