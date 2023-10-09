package com.thanhson.bookhup.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;


@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "genres" )
public class Genre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    @Column(name = "GenreID", nullable = false)
    private long genreID;

    @Column(name = "Title", nullable = false, length = 50)
    private String genreName;

    @ManyToMany(fetch = FetchType.LAZY,cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE
    },mappedBy = "genres")
    @JsonIgnore
    private Set<Book> books = new HashSet<>();

    public long getGenreID() {
        return genreID;
    }

    public String getGenreName() {
        return genreName;
    }

    public void setGenreName(String genreName) {
        this.genreName = genreName;
    }

    public Set<Book> getBooks() {
        return books;
    }

    public void setBooks(Set<Book> books) {
        this.books = books;
    }
}
