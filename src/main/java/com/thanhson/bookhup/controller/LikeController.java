package com.thanhson.bookhup.controller;

import com.thanhson.bookhup.model.Like;
import com.thanhson.bookhup.service.LikeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
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
}