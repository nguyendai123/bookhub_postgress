package com.bookhup.service;

import com.bookhup.dto.request.post.PostRequest;
import com.bookhup.dto.response.post.PostFeedDto;
import com.bookhup.dto.response.user.PostOfUserResponse;
import com.bookhup.model.Post;
import com.bookhup.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PostService {

    Post createPost(PostRequest request, User user);

    Page<PostFeedDto> getFeed(Long userId, int page, int size);

    List<Post> getAllPosts();

    Post updatePost(Long postId, PostRequest post, User user);

    void deletePost(Long postId, User user);

    Post getPost(Long postId);

    Page<PostFeedDto> getUserPosts(Long userId, Pageable pageable);
}
