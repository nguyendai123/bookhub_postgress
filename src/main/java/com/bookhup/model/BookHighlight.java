package com.bookhup.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "book_highlight")
public class BookHighlight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "highlight_id")
    private Long highlightId;

    @ManyToOne
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JoinColumn(name = "book_id")
    private Book book;

    @ManyToOne
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JoinColumn(name = "chapter_id")
    private BookChapter chapter;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String text;

    @Column(name = "page")
    private String pageNumber;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String position;

    @Column(name = "sentiment", length = 20)
    private String sentiment; // POSITIVE | NEGATIVE | NEUTRAL

    @Lob
    @Column(columnDefinition = "TEXT", name = "ai_summary")
    private String aiSummary;

    @Lob
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private List<String> keywords; // JSON

    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "owner_id")
    private Long ownerId;

    @Column(name = "source")
    private String source;  //"USER", // hoặc "AI"
}
