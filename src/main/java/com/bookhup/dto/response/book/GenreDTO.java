package com.bookhup.dto.response.book;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenreDTO {

    private Long genreId;
    private String name;
}
