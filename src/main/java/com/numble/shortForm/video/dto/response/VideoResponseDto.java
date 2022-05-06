package com.numble.shortForm.video.dto.response;

import com.numble.shortForm.hashtag.entity.VideoHash;
import com.numble.shortForm.video.entity.UploadThumbNail;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@Slf4j
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

    private Long duration;

    private Long likes;

    private String description;

    private List<String> tags;

    @QueryProjection
    public VideoResponseDto(Long videoId, Long usersId, String nickname, Long showId, String title, UploadThumbNail uploadThumbNail, boolean isBlock, Long view, LocalDateTime created_at,Long duration,Integer likes) {
        this.videoId = videoId;
        this.usersId = usersId;
        this.nickname = nickname;
        this.showId = showId;
        this.title = title;
        this.uploadThumbNail = uploadThumbNail;
        this.isBlock = isBlock;
        this.view = view;
        this.created_at = created_at.toLocalDate();
        this.duration = duration;
        this.likes = likes.longValue();
    }
    @QueryProjection
    public VideoResponseDto(Long videoId, Long usersId, String nickname, Long showId, String title, UploadThumbNail uploadThumbNail, boolean isBlock, Long view, LocalDateTime created_at, Long duration, Integer likes, String description) {
        this.videoId = videoId;
        this.usersId = usersId;
        this.nickname = nickname;
        this.showId = showId;
        this.title = title;
        this.uploadThumbNail = uploadThumbNail;
        this.isBlock = isBlock;
        this.view = view;
        this.created_at = created_at.toLocalDate();
        this.duration = duration;
        this.likes = likes.longValue();
        this.description = description;

    }
}
