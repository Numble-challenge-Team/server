package com.numble.shortForm.comment.dto.response;

import com.numble.shortForm.user.entity.ProfileImg;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class OriginalComment {

    private Long id;
    private String nickname;
    private String context;
    private String title;
    private String profileUrl;
    private boolean isBlock;
    private Long userId;
    private Long childCount;
    private Long likesCount;
    private boolean isLiked;
    private LocalDate created_at;

    @QueryProjection
    public OriginalComment(Long id, String nickname, String context, String title, String profileUrl, boolean isBlock, Long userId, Long childCount, Integer likesCount, LocalDateTime created_at) {
        this.id = id;
        this.nickname = nickname;
        this.context = context;
        this.title = title;
        this.profileUrl = profileUrl;
        this.isBlock = isBlock;
        this.userId = userId;
        this.childCount = childCount;
        this.likesCount = likesCount.longValue();
        this.created_at = created_at.toLocalDate();
    }

    @QueryProjection
    public OriginalComment(Long id, String nickname, String context, String title, String profileUrl, boolean isBlock, Long userId, Integer likesCount, LocalDateTime created_at) {
        this.id = id;
        this.nickname = nickname;
        this.context = context;
        this.title = title;
        this.profileUrl = profileUrl;
        this.isBlock = isBlock;
        this.userId = userId;
        this.likesCount = likesCount.longValue();
        this.created_at = created_at.toLocalDate();
    }
}
