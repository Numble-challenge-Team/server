package com.numble.shortForm.comment.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChildCommentRequestDto {

    private String context;

    private String title;

    private Long videoId;

    private Long commentId;
}
