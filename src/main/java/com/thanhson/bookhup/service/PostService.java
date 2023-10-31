package com.thanhson.bookhup.service;

import com.thanhson.bookhup.model.Post;
import com.thanhson.bookhup.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostService {
    @Autowired
    private PostRepository postRepository;

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
