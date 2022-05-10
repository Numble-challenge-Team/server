package com.numble.shortForm.comment.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;


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
    private LocalDate created_at;
    private Long likeCount;
    private String ProfileUrl;

    @QueryProjection
    public CommentResponse(long id, String nickname, String context, String title, boolean isBlock, Long userId,
                           Long commentSeq, Long videoId, LocalDateTime created_at, Long likeCount, String profileUrl) {
        this.id = id;
        this.nickname = nickname;
        this.context = context;
        this.title = title;
        this.isBlock = isBlock;
        this.userId = userId;
        this.commentSeq = commentSeq;
        this.videoId = videoId;
        this.created_at = created_at.toLocalDate();
        this.likeCount = likeCount;
        this.ProfileUrl = profileUrl;
    }
}
