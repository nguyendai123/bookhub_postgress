package com.bookhup.controller;

import com.bookhup.model.Like;
import com.bookhup.service.LikeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")

@RequestMapping("/api/likes")
public class LikeController {
    private final LikeService likeService;

    @Autowired
    public LikeController(LikeService likeService) {
        this.likeService = likeService;
    }

    @PostMapping("/create")
    public ResponseEntity<Like> createLike(@RequestBody @Valid Like like) {
        Like createdLike = likeService.save(like);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdLike);
    }

    @DeleteMapping("/delete/{likeID}")
    public ResponseEntity<String> deleteLike(@PathVariable Long likeID) {
        Like like = likeService.getLikeById(likeID);
        if (like == null) {
            return ResponseEntity.notFound().build();
        }
        likeService.delete(like);
        return ResponseEntity.ok("Like deleted successfully.");
    }
    @GetMapping("/post/{postId}")
    public ResponseEntity<List<Like>> getLikesForPost(@PathVariable Long postId) {
        List<Like> likes = likeService.findAllByPostId(postId);
        return ResponseEntity.ok(likes);
    }

}