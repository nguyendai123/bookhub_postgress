package com.bookhup.dto.request.shelf;

import com.bookhup.model.ReadingStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReadingAddRequest {
    private Long bookId;
    private ReadingStatus status; // WANT_TO_READ, READING, FINISHED
    private Integer currentPage;
}
