package com.numble.shortForm.comment.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentRequestDto {


    private String title;

    private String context;

    private Long videoId;

}
