package com.bookhup.service;

import com.bookhup.dto.request.readingProgress.ReadingUpdateRequest;
import com.bookhup.dto.request.shelf.ReadingAddRequest;
import com.bookhup.dto.response.readingProgress.ReadingBookResponse;
import com.bookhup.dto.response.readingProgress.ReadingProgressResponse;
import com.bookhup.dto.response.readingProgress.ReadingResponse;
import com.bookhup.model.ReadingProgress;
import com.bookhup.model.ReadingStatus;
import com.bookhup.model.User;

import java.util.List;

public interface ReadingService {
    List<ReadingBookResponse> getAllReadingProgress(User user);

    List<ReadingBookResponse> getByStatus(
            User user,
            ReadingStatus status
    );
    // ====== Add book to reading shelf ======
    ReadingProgress addToShelf(User user, ReadingAddRequest req);

    // ====== Update progress ======
    ReadingProgressResponse updateProgress(User user, ReadingUpdateRequest req);

    ReadingResponse getReadingProgress(User user, Long bookId);
    ReadingResponse getReadingProgress(Long userId, Long bookId);

}
