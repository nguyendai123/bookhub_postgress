//package com.bookhup.service.impl;
//
//import com.bookhup.model.Like;
//import com.bookhup.model.Post;
//import com.bookhup.model.User;
//import com.bookhup.repository.LikeRepository;
//import com.bookhup.service.LikeService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//
//@RequiredArgsConstructor
//@Service
//public class LikeServiceImpl implements LikeService {
//
//    private final LikeRepository likeRepository;
//
//    @Override
//    public List<Like> findAllByPostId(Long postId) {
//        return likeRepository.findAllByPost_PostID(postId);
//    }
//
//    @Override
//    public Like save(Like like) {
//        return likeRepository.save(like);
//    }
//
//    @Override
//    public void delete(Like like) {
//        likeRepository.delete(like);
//    }
//
//    @Override
//    public Like getLikeById(Long likeId) {
//        return likeRepository.findById(likeId).orElse(null);
//    }
//
//    @Override
//    public Like getLikeByUserIdAndPostId(Post post, User user) {
//        return likeRepository.findByUserAndPost(user, post);
//    }
//
//    @Override
//    public void deleteLike(Long likeId) {
//        likeRepository.deleteById(likeId);
//    }
//
//    @Override
//    @Transactional
//    public void deleteLikesByPostId(Long postId) {
//        likeRepository.deleteByPostId(postId);
//    }
//}
