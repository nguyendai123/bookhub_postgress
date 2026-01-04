package com.bookhup.service;

import com.bookhup.dto.request.ai.highLight.HighlightRequest;
import com.bookhup.model.BookChapter;
import com.bookhup.model.BookHighlight;
import com.bookhup.model.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface HighlightService {
    @Transactional
    BookHighlight highlight(User user, HighlightRequest req);

    @Transactional
    void autoHighlightChapter(User user, BookChapter chapter);

    List<BookHighlight> getHighlights(Long chapterId, User user);
}
