package com.bookhup.dto.response.readingProgress;

import com.bookhup.model.Book;
import lombok.Data;

@Data
public class BookDTO {
    private Long id;
    private String title;
    private String image;
    private String authorName;
    private Integer totalPages;
    private Float averageRating;
    private Integer numberOfReviews;

    public BookDTO(Book book) {
        this.id = book.getBookId();
        this.title = book.getTitle();
        this.image = book.getCoverUrl();
        this.authorName = book.getAuthor().getName();
        this.totalPages = book.getTotalPages();
        this.averageRating = Math.round(book.getAvgRating() * 10) / 10f;
        this.numberOfReviews = book.getTotalReviews();

    }
}
