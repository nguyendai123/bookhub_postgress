package com.bookhup.service;

import com.bookhup.dto.request.readingProgress.ReadingUpdateRequest;
import com.bookhup.dto.request.shelf.ReadingAddRequest;
import com.bookhup.dto.response.readingProgress.ReadingProgressResponse;
import com.bookhup.model.ReadingProgress;

public interface ReadingService {
    // ====== Add book to reading shelf ======
    ReadingProgress addToShelf(Long userId, ReadingAddRequest req);

    // ====== Update progress ======
    ReadingProgressResponse updateProgress(Long userId, ReadingUpdateRequest req);
}
