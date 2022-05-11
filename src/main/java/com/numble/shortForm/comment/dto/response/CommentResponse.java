package com.numble.shortForm.comment.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class CommentResponse {

    private long id;
    private String nickname;
    private String context;
    private String title;
    private boolean isBlock;
    private Long userId;
    private Long commentSeq;
    private Long videoId;

    @QueryProjection
    public CommentResponse(long id, String nickname, String context, String title, boolean isBlock, Long userId, Long commentSeq, Long videoId) {
        this.id = id;
        this.nickname = nickname;
        this.context = context;
        this.title = title;
        this.isBlock = isBlock;
        this.userId = userId;
        this.commentSeq = commentSeq;
        this.videoId = videoId;
    }


}
