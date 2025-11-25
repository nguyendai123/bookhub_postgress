package com.bookhup.dto.request.book;

import lombok.Data;

import java.util.List;

@Data
public class BookCreateRequest {

    private String isbn;
    private String title;
    private Long authorId;
    private String language;
    private String description;
    private String coverUrl;

    private List<Long> genreIds;

    private List<ChapterRequest> chapters;

    private List<MediaAssetRequest> mediaAssets;

    @Data
    public static class ChapterRequest {
        private String chapterTitle;
        private Integer chapterOrder;
        private String textContent;
        private String audioUrl;
        private Float duration;
    }

    @Data
    public static class MediaAssetRequest {
        private String fileUrl;
        private String type;   // image, audio, pdf
    }
}
