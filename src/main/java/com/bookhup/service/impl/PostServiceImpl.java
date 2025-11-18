package com.bookhup.service.impl;

import com.bookhup.repository.PostRepository;
import com.bookhup.model.Post;
import com.bookhup.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;


    public Post getPostById(Long postID) {
        return postRepository.findByPostID(postID);
    }

    public List<Post> findAll() {
        return postRepository.findAllByOrderByCreateDateDesc();
    }

    public List<Post> findAllByUser(long userID) {
        return postRepository.findAllByUser_UserIDOrderByCreateDateDesc(userID);
    }

    public long save(Post post) {
        return postRepository.saveAndFlush(post).getPostID();
    }

    public void delete(Post post) {
        postRepository.delete(post);
    }

}
