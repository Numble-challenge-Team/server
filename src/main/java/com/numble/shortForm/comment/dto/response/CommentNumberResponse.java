package com.numble.shortForm.comment.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Getter
public class commentNumberResponse {

    private long id;
    private String nickname;
    private String context;
    private String title;
    private boolean isBlock;
    private Long userId;
    private Long commentSeq;
    private Long videoId;
    private Long commentCount;
    private Long likeCount;
    private LocalDate created_at;

    @Setter
    private boolean isReComment;

    @QueryProjection
    public commentNumberResponse(long id, String nickname, String context, String title, boolean isBlock, Long userId,
                                 Long commentSeq, Long videoId, Long commentCount, Long likeCount, LocalDateTime created_at) {
        this.id = id;
        this.nickname = nickname;
        this.context = context;
        this.title = title;
        this.isBlock = isBlock;
        this.userId = userId;
        this.commentSeq = commentSeq;
        this.videoId = videoId;
        this.commentCount = commentCount;
        this.likeCount = likeCount;
        this.created_at = created_at.toLocalDate();
    }
}
