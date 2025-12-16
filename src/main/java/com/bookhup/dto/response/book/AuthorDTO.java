package com.bookhup.dto.response.book;

import com.bookhup.model.Author;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthorDTO {

    private Long authorId;
    private String name;
    private String bio;
    private String country;
    public static AuthorDTO toAuthorDTO(Author author) {

        if (author == null) {
            return null;
        }

        return AuthorDTO.builder()
                .authorId(author.getAuthorId())
                .name(author.getName())
                .bio(author.getBio())
                .country(author.getCountry())
                .build();
    }

}

