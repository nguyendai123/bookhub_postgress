package com.bookhup.dto.response.book;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BookPdfResponse {
    private String pdfUrl;
    private Integer currenPage;
    private Integer totalPages;
}
