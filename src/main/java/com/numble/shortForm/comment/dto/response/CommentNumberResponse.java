package com.numble.shortForm.comment.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.Setter;


@Getter
public class CommentNumberResponse {

    private long id;
    private String nickname;
    private String context;
    private String title;
    private boolean isBlock;
    private Long userId;
    private Long commentSeq;
    private Long videoId;
    private Long commentCount;

    @Setter
    private boolean isReComment;

    @QueryProjection
    public CommentNumberResponse(long id, String nickname, String context, String title, boolean isBlock, Long userId,
                                 Long commentSeq, Long videoId, Long commentCount) {
        this.id = id;
        this.nickname = nickname;
        this.context = context;
        this.title = title;
        this.isBlock = isBlock;
        this.userId = userId;
        this.commentSeq = commentSeq;
        this.videoId = videoId;
        this.commentCount = commentCount;
    }
}
