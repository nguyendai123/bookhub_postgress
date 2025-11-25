package com.bookhup.service.impl;

import com.bookhup.dto.request.book.BookCreateRequest;
import com.bookhup.model.*;
import com.bookhup.repository.*;
import com.bookhup.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final GenreRepository genreRepository;
    private final BookChapterRepository chapterRepository;
    private final BookMediaAssetRepository mediaRepository;

    @Override
    public List<Book> search(String keyword) {
        return bookRepository.searchBooks(keyword);
    }

    @Override
    public Book getDetail(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found"));
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
}

