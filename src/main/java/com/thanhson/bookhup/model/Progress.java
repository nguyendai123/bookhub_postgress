package com.thanhson.bookhup.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="progress")
public class Progress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long progressID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BookID")
    private Book book;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UserID")
    private User userProgress;

    @Column(name = "Status")
    private String status;

    @Column(name = "ReadPage")
    private int readPage;
}
