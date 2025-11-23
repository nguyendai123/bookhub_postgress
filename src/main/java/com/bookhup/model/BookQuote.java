package com.bookhup.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "book_quote")
public class BookQuote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "quote_id")
    private Long quoteId;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    @Lob
    @Column(name = "quote_text")
    private String quoteText;

    @ManyToOne
    @JoinColumn(name = "added_by")
    private User addedBy;

    @Column(name = "source_chapter", length = 100)
    private String sourceChapter;

    @Column(name = "ai_generated")
    private Boolean aiGenerated;

    @Column(name = "popularity_score")
    private Integer popularityScore;
}
