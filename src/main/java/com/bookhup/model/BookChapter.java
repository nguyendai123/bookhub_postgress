package com.bookhup.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "book_chapter")
public class BookChapter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chapter_id")
    private Long chapterId;

    @ManyToOne
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JoinColumn(name = "book_id")
    private Book book;

    @Column(name = "chapter_title", length = 255)
    private String chapterTitle;

    @Column(name = "chapter_order")
    private Integer chapterOrder;

    @Lob
    @Column(name = "text_content")
    private String textContent;

    @Column(name = "audio_url", length = 255)
    private String audioUrl;

    @Column(name = "duration")
    private Float duration;

    @Lob
    @Column(name = "embedding_vector")
    private byte[] embeddingVector; // BLOB for mariadb
}
