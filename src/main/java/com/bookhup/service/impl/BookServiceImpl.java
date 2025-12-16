package com.bookhup.service.impl;

import com.bookhup.dto.request.book.BookCreateRequest;
import com.bookhup.dto.response.book.*;
import com.bookhup.model.*;
import com.bookhup.repository.*;
import com.bookhup.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final GenreRepository genreRepository;
    private final BookChapterRepository chapterRepository;
    private final BookMediaAssetRepository mediaRepository;

    @Override
    public Page<BookShelfDTO> search(
            String keyword,
            User user,
            int page,
            int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("title").ascending());
        return toDTO(bookRepository.searchBooksWithUserStatus(keyword, user, pageable), user);
    }


    @Override
    public BookDetailDTO getDetail(Long id) {
        return toDetailDTO(bookRepository.findDetailByBookId(id)
                .orElseThrow(() -> new RuntimeException("Book not found")));
    }

    @Transactional
    @Override
    public Book createBook(BookCreateRequest req) {
        bookRepository.findByIsbn(req.getIsbn())
                .ifPresent(b -> {
                    throw new RuntimeException(
                            "Sách với ISBN " + req.getIsbn() + " đã tồn tại: " + b.getTitle()
                    );
                });
        boolean exists = bookRepository.existsByTitleIgnoreCaseAndAuthor_AuthorId(req.getTitle(), req.getAuthorId());
        if (exists) {
            throw new RuntimeException("Sách đã tồn tại theo (title + author)");
        }

        // 1. Tìm Author
        Author author = authorRepository.findById(req.getAuthorId())
                .orElseThrow(() -> new RuntimeException("Author not found"));

        // 2. Tạo Book
        Book book = Book.builder()
                .isbn(req.getIsbn())
                .title(req.getTitle())
                .author(author)
                .language(req.getLanguage())
                .description(req.getDescription())
                .coverUrl(req.getCoverUrl())
                .avgRating(0f)
                .totalReviews(0)
                .createdAt(LocalDateTime.now())
                .build();

        // 3. Gán Genres
        if (req.getGenreIds() != null) {
            Set<Genre> genres = new HashSet<>();
            for (Long gid : req.getGenreIds()) {
                Genre g = genreRepository.findById(gid)
                        .orElseThrow(() -> new RuntimeException("Genre not found: " + gid));
                genres.add(g);
            }
            book.setGenres(genres);
        }

        book = bookRepository.save(book);

        // 4. Lưu Chapters
        if (req.getChapters() != null) {
            for (BookCreateRequest.ChapterRequest c : req.getChapters()) {
                BookChapter chapter = BookChapter.builder()
                        .book(book)
                        .chapterTitle(c.getChapterTitle())
                        .chapterOrder(c.getChapterOrder())
                        .textContent(c.getTextContent())
                        .audioUrl(c.getAudioUrl())
                        .duration(c.getDuration())
                        .build();

                chapterRepository.save(chapter);
            }
        }

        // 5. Lưu Media Assets
        if (req.getMediaAssets() != null) {
            for (BookCreateRequest.MediaAssetRequest m : req.getMediaAssets()) {
                BookMediaAsset asset = BookMediaAsset.builder()
                        .book(book)
                        .fileUrl(m.getFileUrl())
                        .type(m.getType()) // image, audio, pdf
                        .build();

                mediaRepository.save(asset);
            }
        }

        return book;
    }

    public Book getBook(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found"));
    }

    public static BookDetailDTO toDetailDTO(Book book) {

        return BookDetailDTO.builder()
                .bookId(book.getBookId())
                .isbn(book.getIsbn())
                .title(book.getTitle())
                .language(book.getLanguage())
                .description(book.getDescription())
                .coverUrl(book.getCoverUrl())
                .avgRating(book.getAvgRating())
                .totalReviews(book.getTotalReviews())
                .totalPages(book.getTotalPages())
                .createdAt(book.getCreatedAt())

                .author(AuthorDTO.toAuthorDTO(book.getAuthor()))

                .genres(book.getGenres().stream()
                        .map(g -> new GenreDTO(g.getGenreId(), g.getName()))
                        .collect(Collectors.toSet()))

                .bookReviews(book.getBookReviews().stream()
                        .map(r -> BookReviewDTO.builder()
                                .reviewId(r.getReviewId())
                                .rating(r.getRating())
                                .comment(r.getComment())
                                .imageUrl(r.getImageUrl())
                                .lang(r.getLang())
                                .likesCount(r.getLikesCount())
                                .aiSentimentScore(r.getAiSentimentScore())
                                .translatedText(r.getTranslatedText())
                                .createdAt(r.getCreatedAt())
                                .userId(r.getUser() != null ? r.getUser().getUserId() : null)
                                .userName(r.getUser() != null ? r.getUser().getUsername() : null)
                                .build())
                        .collect(Collectors.toSet()))

                .chapters(book.getChapters().stream()
                        .map(c -> BookChapterDTO.builder()
                                .chapterId(c.getChapterId())
                                .chapterOrder(c.getChapterOrder())
                                .chapterTitle(c.getChapterTitle())
                                .textContent(c.getTextContent())
                                .audioUrl(c.getAudioUrl())
                                .duration(c.getDuration())
                                .build())
                        .collect(Collectors.toSet()))

                .mediaAssets(book.getMediaAssets().stream()
                        .map(a -> new BookMediaAssetDTO(
                                a.getAssetId(),
                                a.getFileUrl(),
                                a.getType()))
                        .collect(Collectors.toSet()))

                .highlights(book.getHighlights().stream()
                        .map(h -> BookHighlightDTO.builder()
                                .highlightId(h.getHighlightId())
                                .userId(h.getUser() != null ? h.getUser().getUserId() : null)
                                .chapterId(h.getChapter() != null ? h.getChapter().getChapterId() : null)
                                .text(h.getText())
                                .position(h.getPosition())
                                .sentiment(h.getSentiment())
                                .aiSummary(h.getAiSummary())
                                .keywords(h.getKeywords())
                                .build())
                        .collect(Collectors.toSet()))

                .quotes(book.getQuotes().stream()
                        .map(q -> BookQuoteDTO.builder()
                                .quoteId(q.getQuoteId())
                                .quoteText(q.getQuoteText())
                                .sourceChapter(q.getSourceChapter())
                                .aiGenerated(q.getAiGenerated())
                                .popularityScore(q.getPopularityScore())
                                .addedByUserId(q.getAddedBy() != null ? q.getAddedBy().getUserId() : null)
                                .build())
                        .collect(Collectors.toSet()))

                .readingProgresses(book.getReadingProgresses().stream()
                        .map(p -> ReadingProgressDTO.builder()
                                .progressId(p.getProgressId())
                                .userId(p.getUser() != null ? p.getUser().getUserId() : null)
                                .userName(p.getUser() != null ? p.getUser().getUsername() : null)
                                .readingStatus(
                                        p.getReadingStatus() != null ? p.getReadingStatus().name() : null)
                                .currentPage(p.getCurrentPage())
                                .totalPages(p.getTotalPages())
                                .percentDone(p.getPercentDone())
                                .avgReadSpeed(p.getAvgReadSpeed())
                                .lastDevice(p.getLastDevice())
                                .focusScore(p.getFocusScore())
                                .startDate(p.getStartDate())
                                .finishedDate(p.getFinishedDate())
                                .lastUpdated(p.getLastUpdated())
                                .build())
                        .collect(Collectors.toSet()))

                .build();
    }

    public static Page<BookShelfDTO> toDTO(Page<Book> page, User user) {
        return page.map(b -> {
            ReadingProgress rp = b.getReadingProgresses().stream()
                    .filter(p -> p.getUser().equals(user))
                    .findFirst()
                    .orElse(null);

            return BookShelfDTO.builder()
                    .bookId(b.getBookId())
                    .title(b.getTitle())
                    .isbn(b.getIsbn())
                    .coverUrl(b.getCoverUrl())
                    .authorName(
                            b.getAuthor() != null ? b.getAuthor().getName() : null
                    )
                    .avgRating(b.getAvgRating())
                    .totalReviews(b.getTotalReviews())
                    .totalPages(b.getTotalPages())
                    .readingStatus(rp != null ? rp.getReadingStatus().name() : null)
                    .currentPage(rp != null ? rp.getCurrentPage() : null)
                    .percentDone(rp != null ? rp.getPercentDone() : null)
                    .genres(
                            b.getGenres().stream()
                                    .map(g -> new GenreDTO(
                                            g.getGenreId(),
                                            g.getName()
                                    ))
                                    .toList()
                    )
                    .build();
        });
    }
}

