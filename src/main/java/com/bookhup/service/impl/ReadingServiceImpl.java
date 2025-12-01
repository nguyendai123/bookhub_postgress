package com.bookhup.service.impl;

import com.bookhup.dto.request.readingProgress.ReadingUpdateRequest;
import com.bookhup.dto.request.shelf.ReadingAddRequest;
import com.bookhup.dto.response.readingProgress.ReadingProgressResponse;
import com.bookhup.model.Book;
import com.bookhup.model.ReadingProgress;
import com.bookhup.model.User;
import com.bookhup.repository.BookRepository;
import com.bookhup.repository.ReadingProgressRepository;
import com.bookhup.repository.UserRepository;
import com.bookhup.service.ReadingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.bookhup.model.ReadingStatus.*;

@Service
@RequiredArgsConstructor
public class ReadingServiceImpl implements ReadingService {

    private final ReadingProgressRepository repo;
    private final UserRepository userRepo;
    private final BookRepository bookRepo;

    // ====== Add book to reading shelf ======
    @Override
    public ReadingProgress addToShelf(Long userId, ReadingAddRequest req) {

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Book book = bookRepo.findById(req.getBookId())
                .orElseThrow(() -> new RuntimeException("Book not found"));

        int totalPages = book.getTotalPages();

        ReadingProgress progress = repo
                .findByUser_UserIdAndBook_BookId(userId, req.getBookId())
                .orElse(ReadingProgress.builder()
                        .user(user)
                        .book(book)
                        .currentPage(0)
                        .percentDone(0f)
                        .startDate(LocalDateTime.now())
                        .build()
                );

        progress.setReadingStatus(req.getStatus());
        progress.setLastUpdated(LocalDateTime.now());

        // ==============================
        //  XỬ LÝ percentDone THEO STATUS
        // ==============================

        switch (req.getStatus()) {

            case WANT_TO_READ:
                progress.setCurrentPage(0);
                progress.setPercentDone(0f);
                break;

            case READING:
                Integer currentPage = req.getCurrentPage() != null ? req.getCurrentPage() : progress.getCurrentPage();
                if (currentPage == null) currentPage = 0;

                if (totalPages > 0)
                    progress.setPercentDone((currentPage * 100f) / totalPages);
                else
                    progress.setPercentDone(0f);
                break;

            case FINISHED:
                progress.setCurrentPage(totalPages);
                progress.setPercentDone(100f);
                progress.setFinishedDate(LocalDateTime.now());
                break;
        }

        return repo.save(progress);
    }

    // ====== Update progress ======
    @Override
    public ReadingProgressResponse updateProgress(Long userId, ReadingUpdateRequest req) {

        ReadingProgress progress = repo.findByUser_UserIdAndBook_BookId(userId, req.getBookId())
                .orElseThrow(() -> new RuntimeException("You haven't added this book to your shelf"));

        Book book = bookRepo.findById(req.getBookId())
                .orElseThrow(() -> new RuntimeException("Book not found"));

        // Nếu trạng thái vẫn là WANT_TO_READ → chuyển sang READING
        if (WANT_TO_READ.equals(progress.getReadingStatus())) {
            progress.setReadingStatus(READING);
            progress.setStartDate(LocalDateTime.now());
        }

        Optional.ofNullable(req.getCurrentPage())
                .ifPresent(progress::setCurrentPage);
        Optional.ofNullable(req.getDevice())
                .ifPresent(progress::setLastDevice);
        progress.setLastUpdated(LocalDateTime.now());

        // Percent Done
        int totalPages = book.getTotalPages(); //lưu tổng trang ở Book
        if (totalPages > 0) {
            progress.setPercentDone((req.getCurrentPage() * 100f) / totalPages);
        }

        if (progress.getPercentDone() != null && progress.getPercentDone() >= 100) {
            progress.setReadingStatus(FINISHED);
            progress.setFinishedDate(LocalDateTime.now());
        }
        // 2) Đang đọc → READING
        else if (progress.getPercentDone() != null && progress.getPercentDone() > 0 && progress.getPercentDone() < 100) {
            progress.setReadingStatus(READING);
        }

        repo.save(progress);

        return new ReadingProgressResponse(
                req.getBookId(),
                progress.getReadingStatus(),
                progress.getCurrentPage(),
                progress.getPercentDone(),
                progress.getLastUpdated()
        );
    }
}

