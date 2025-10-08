package com.bookhup.controller;

import com.bookhup.model.Book;
import com.bookhup.model.Progress;
import com.bookhup.model.User;
import com.bookhup.service.BookService;
import com.bookhup.service.ProgressService;
import com.bookhup.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")

@AllArgsConstructor
@RequestMapping("/api")
public class ProgressController {
    @Autowired
    private ProgressService progressService;
    private UserService userService;
    private BookService bookService;

    @GetMapping("/progresses")
    public List<Progress> getAllProgresses() {
        return progressService.getAllProgresses();
    }
    @PostMapping("/progresses/create")
    public ResponseEntity<Progress> createProgress(@RequestBody @Valid Progress progress) {
        Progress createdProgress = progressService.save(progress);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProgress);
    }

    @DeleteMapping("/progresses/delete/{progressID}")
    public ResponseEntity<String> deleteProgress(@PathVariable Long progressID) {
        Progress progress = progressService.getProgressById(progressID);
        if (progress == null) {
            return ResponseEntity.notFound().build();
        }

        progressService.delete(progress);
        return ResponseEntity.ok("Progress deleted successfully.");
    }
    @GetMapping("/progresses/{userId}/{bookId}")
    public ResponseEntity<Progress> getProgressByUserIdAndBookId(
            @PathVariable("userId") Long userId,
            @PathVariable("bookId") Long bookId
    ) {
        // Retrieve the User and Book entities based on their IDs
        User userProgress = userService.findById(userId);
        Book book = bookService.findById(bookId);

        if (userProgress == null || book == null) {
            return ResponseEntity.notFound().build();
        }

        Progress progress = progressService.findByUserProgressAndBook(userProgress, book);

        if (progress == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(progress);
    }

}