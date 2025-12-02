package com.bookhup.dto.request.comment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequest {
    private Long postId;
    private String content;
    private Long parentId; // null nếu không phải reply
}
