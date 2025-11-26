package com.bookhup.service;

import com.bookhup.dto.request.hashtag.PostHashtagRequest;
import com.bookhup.model.Post;
import com.bookhup.model.User;

public interface HashtagService {
    Post addHashtags(PostHashtagRequest req, User currentUser);
}
