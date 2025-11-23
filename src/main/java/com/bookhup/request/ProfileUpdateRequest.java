package com.bookhup.request;

import lombok.Data;

@Data
public class ProfileUpdateRequest {
    private String avatarUrl;
    private String bio;
    private String favoriteGenres; // JSON
    private String readingPattern;
    private String preferredLanguage;
    private Float avgReadTimePerDay;
    private String socialLinks; // JSON
}

