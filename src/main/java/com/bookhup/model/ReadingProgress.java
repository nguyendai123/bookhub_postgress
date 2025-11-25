package com.bookhup.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "reading_progress")
public class ReadingProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "progress_id")
    private Long progressId;

    @ManyToOne
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JoinColumn(name = "book_id")
    private Book book;

    @Column(name = "reading_status", length = 20)
    private String readingStatus; // WANT_TO_READ, READING, FINISHED

    @Column(name = "current_page")
    private Integer currentPage;

    @Column(name = "percent_done")
    private Float percentDone;

    @Column(name = "avg_read_speed")
    private Float avgReadSpeed;

    @Column(name = "last_device", length = 50)
    private String lastDevice;

    @Column(name = "focus_score")
    private Float focusScore;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "finished_date")
    private LocalDateTime finishedDate;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;
}
