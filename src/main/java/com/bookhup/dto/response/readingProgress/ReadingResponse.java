package com.bookhup.dto.response.readingProgress;


import com.bookhup.model.Book;
import lombok.Data;

@Data
public class ReadingResponse {
    private BookDTO book;
    private Integer readPage;
}

