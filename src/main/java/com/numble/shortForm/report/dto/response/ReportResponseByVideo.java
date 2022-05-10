package com.numble.shortForm.report.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class ReportResponseByVideo {
    private Long id;
    private String title;
    private String videoUrl;
    private Long view;
    private LocalDate created_at;
    private boolean isBlock;
    private String nickname;
    private Long reportCount;

    @QueryProjection
    public ReportResponseByVideo(Long id, String title, String videoUrl,
                                 Long view, LocalDateTime created_at, boolean isBlock,
                                 String nickname, Long reportCount ) {
        this.id = id;
        this.title = title;
        this.videoUrl = videoUrl;
        this.view = view;
        this.created_at = created_at.toLocalDate();
        this.isBlock = isBlock;
        this.nickname = nickname;
        this.reportCount = reportCount;
    }
}
