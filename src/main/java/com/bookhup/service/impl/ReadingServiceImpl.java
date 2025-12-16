package com.bookhup.service.impl;

import com.bookhup.dto.request.readingProgress.ReadingUpdateRequest;
import com.bookhup.dto.request.shelf.ReadingAddRequest;
import com.bookhup.dto.response.book.GenreDTO;
import com.bookhup.dto.response.readingProgress.BookDTO;
import com.bookhup.dto.response.readingProgress.ReadingBookResponse;
import com.bookhup.dto.response.readingProgress.ReadingProgressResponse;
import com.bookhup.dto.response.readingProgress.ReadingResponse;
import com.bookhup.exception.AppException;
import com.bookhup.exception.ErrorCode;
import com.bookhup.model.*;
import com.bookhup.repository.BookRepository;
import com.bookhup.repository.ReadingProgressRepository;
import com.bookhup.repository.UserRepository;
import com.bookhup.service.ReadingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.bookhup.model.ReadingStatus.*;

@Service
@RequiredArgsConstructor
public class ReadingServiceImpl implements ReadingService {

    private final ReadingProgressRepository repo;
    private final UserRepository userRepo;
    private final BookRepository bookRepo;

    @Override
    public List<ReadingBookResponse> getAllReadingProgress(User user) {

        List<ReadingProgress> progresses =
                repo.findByUser(user);

        return progresses.stream()
                .map(this::toReadingBookResponse)
                .toList();
    }

    @Override
    public List<ReadingBookResponse> getByStatus(
            User user,
            ReadingStatus status
    ) {
        return repo
                .findByUserAndReadingStatus(user, status)
                .stream()
                .map(this::toReadingBookResponse)
                .toList();
    }


    // ====== Add book to reading shelf ======
    @Override
    public ReadingProgress addToShelf(User user, ReadingAddRequest req) {

        Book book = bookRepo.findById(req.getBookId())
                .orElseThrow(() -> new RuntimeException("Book not found"));

        int totalPages = book.getTotalPages();

        ReadingProgress progress = repo
                .findByUser_UserIdAndBook_BookId(user.getUserId(), req.getBookId())
                .orElse(ReadingProgress.builder()
                        .user(user)
                        .book(book)
                        .currentPage(req.getCurrentPage())
                        .percentDone(0f)
                        .totalPages(totalPages)
                        .startDate(LocalDateTime.now())
                        .build()
                );

        progress.setCurrentPage(req.getCurrentPage());
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
                    progress.setPercentDone(Math.round(((currentPage * 100f) / totalPages) * 10) / 10f);
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
    public ReadingProgressResponse updateProgress(User user, ReadingUpdateRequest req) {

        ReadingProgress progress = repo.findByUser_UserIdAndBook_BookId(user.getUserId(), req.getBookId())
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
        progress.setTotalPages(totalPages);
        if (totalPages > 0) {
            progress.setPercentDone(Math.round(((req.getCurrentPage() * 100f) / totalPages) * 10) / 10f);
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

    @Override
    public ReadingResponse getReadingProgress(User user, Long bookId) {
        Book book = bookRepo.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        Optional<ReadingProgress> progressOpt =
                repo.findByUser_UserIdAndBook_BookId(user.getUserId(), bookId);

        ReadingResponse resp = new ReadingResponse();
        resp.setBook(new BookDTO(book));
        resp.setReadPage(progressOpt.map(ReadingProgress::getCurrentPage).orElse(null));

        return resp;
    }
    @Override
    public ReadingResponse getReadingProgress(Long userId, Long bookId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Book book = bookRepo.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        Optional<ReadingProgress> progressOpt =
                repo.findByUser_UserIdAndBook_BookId(userId, bookId);

        ReadingResponse resp = new ReadingResponse();
        resp.setBook(new BookDTO(book));
        resp.setReadPage(progressOpt.map(ReadingProgress::getCurrentPage).orElse(null));

        return resp;
    }
    private ReadingBookResponse toReadingBookResponse(
            ReadingProgress progress
    ) {
        Book book = progress.getBook();

        return ReadingBookResponse.builder()
                // book
                .bookId(book.getBookId())
                .title(book.getTitle())
                .coverUrl(book.getCoverUrl())
                .authorName(
                        book.getAuthor() != null
                                ? book.getAuthor().getName()
                                : null
                )
                .avgRating(book.getAvgRating())
                .totalReviews(book.getTotalReviews())
                .totalPages(book.getTotalPages())
                .genres(book.getGenres().stream()
                                .map(g -> new GenreDTO(
                                        g.getGenreId(),
                                        g.getName()
                                ))
                                .toList()
                )

                // progress
                .readingStatus(progress.getReadingStatus())
                .currentPage(progress.getCurrentPage())
                .percentDone(progress.getPercentDone())
                .lastUpdated(progress.getLastUpdated())
                .build();
    }

}

