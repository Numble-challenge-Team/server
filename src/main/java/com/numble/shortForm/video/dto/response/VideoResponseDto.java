package com.numble.shortForm.video.dto.response;

import com.numble.shortForm.video.entity.UploadThumbNail;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class VideoResponseDto {

    private Long videoId;

    private Long usersId;

    private String nickname;

    private Long showId;

    private String title;

    private UploadThumbNail uploadThumbNail;

    private boolean isBlock;

    private Long view;

    private LocalDate created_at;

    @QueryProjection
    public VideoResponseDto(Long videoId, Long usersId, String nickname, Long showId, String title, UploadThumbNail uploadThumbNail, boolean isBlock, Long view, LocalDateTime created_at) {
        this.videoId = videoId;
        this.usersId = usersId;
        this.nickname = nickname;
        this.showId = showId;
        this.title = title;
        this.uploadThumbNail = uploadThumbNail;
        this.isBlock = isBlock;
        this.view = view;
        this.created_at = created_at.toLocalDate();
    }
}
