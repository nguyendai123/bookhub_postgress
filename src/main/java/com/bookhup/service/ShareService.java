package com.bookhup.service;

import com.bookhup.dto.request.share.ShareRequest;
import com.bookhup.model.Share;
import com.bookhup.model.User;

public interface ShareService {
    Share sharePost(ShareRequest req, User user);
}
