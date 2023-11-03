package com.thanhson.bookhup.service;

import com.thanhson.bookhup.model.Like;
import com.thanhson.bookhup.model.Post;
import com.thanhson.bookhup.model.User;
import com.thanhson.bookhup.repository.LikeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LikeService {
    @Autowired
    private LikeRepository likeRepository;

    public List<Like> findAllByPostId(Long postID) {
        return likeRepository.findAllByPost_PostID(postID);
    }

    public Like save(Like like) {
        return likeRepository.save(like);
    }

    public void delete(Like like) {
        likeRepository.delete(like);
    }

    public Like getLikeById(Long likeId) {
        return likeRepository.findById(likeId).orElse(null);
    }


    public Like getLikeByUserIdAndPostId(Post post, User user) {
        return likeRepository.findByUserAndPost(user, post);
    }

    public void deleteLike(Long likeID) {
        likeRepository.deleteById(likeID);
    }






    @Transactional
    public void deleteLikesByPostId(Long postId) {
        likeRepository.deleteByPostId(postId);
    }
}
