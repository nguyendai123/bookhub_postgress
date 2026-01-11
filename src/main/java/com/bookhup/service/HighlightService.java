package com.bookhup.service;

import com.bookhup.dto.request.ai.highLight.HighlightRequest;
import com.bookhup.model.BookChapter;
import com.bookhup.model.BookHighlight;
import com.bookhup.model.ReadingProgress;
import com.bookhup.model.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface HighlightService {
    void tryHighlightCurrentChapter(ReadingProgress progress);

    void tryHighlightNextChapter(ReadingProgress progress);

    @Transactional
    BookHighlight highlight(User user, HighlightRequest req);

    @Transactional
    void autoHighlightChapter(Long chapterId);

    List<BookHighlight> getHighlights(Long chapterId, User user);
}
