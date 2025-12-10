package com.bookhup.service;

import com.bookhup.dto.request.readingProgress.ReadingUpdateRequest;
import com.bookhup.dto.request.shelf.ReadingAddRequest;
import com.bookhup.dto.response.readingProgress.ReadingProgressResponse;
import com.bookhup.dto.response.readingProgress.ReadingResponse;
import com.bookhup.model.ReadingProgress;
import com.bookhup.model.User;

public interface ReadingService {
    // ====== Add book to reading shelf ======
    ReadingProgress addToShelf(User user, ReadingAddRequest req);

    // ====== Update progress ======
    ReadingProgressResponse updateProgress(User user, ReadingUpdateRequest req);

    ReadingResponse getReadingProgress(User user, Long bookId);
    ReadingResponse getReadingProgress(Long userId, Long bookId);
}
