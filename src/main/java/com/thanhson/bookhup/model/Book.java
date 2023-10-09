package com.thanhson.bookhup.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "books")

public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    @Column(name = "BookID", nullable = false)
    private long bookID;

    @Column(name = "Title", length = 255, nullable = false)
    private String title;

    @Column(name = "Image",columnDefinition = "LONGTEXT")
    private String image;

    @Column(name = "Author", length = 50, nullable = false)
    private String author;

    @Column(name = "ISBN", length = 50, nullable = false)
    private String isbn;

    @Column(name = "Summary", nullable = false)
    private String summary;

    @Column(name = "Page")
    private int page;
    @Column(name = "Rate")
    private double rate;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE
    })
    @JoinTable(name = "book_genre", joinColumns = { @JoinColumn(name = "BookID") }, inverseJoinColumns = {
            @JoinColumn(name = "GenreID") })
    private Set<Genre> genres = new HashSet<>();

    @OneToMany(mappedBy = "book")
    Set<Progress> progresses;

    public void addGenre(Genre genre) {
        this.genres.add(genre);
        genre.getBooks().add(this);
    }

    public void removeGenre(long genreID) {
        Genre genre = this.genres.stream().filter(t -> t.getGenreID() == genreID).findFirst().orElse(null);
        if (genre != null) {
            this.genres.remove(genre);
            genre.getBooks().remove(this);
        }
    }
}
