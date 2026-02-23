package com.bookhup.controller.dto;

import com.bookhup.model.Book;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class BulkBookCreateResponse {
    private List<Book> successBooks;
    private List<String> errors;
}
