package com.numble.shortForm.report.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.Setter;
import org.joda.time.LocalDateTime;

@Getter
public class reportResponseByUser {

    private Long userId;
    private String nickanme;
    private Long videoId;
    private String videoTitle;
    private String videoUrl;
    private Long commentId;
    private String commentContext;
    private String commentTitle;
    private LocalDateTime createdAt;
    private Long reportCount;

    @Setter
    private boolean isFiveReport;



    @QueryProjection
    public reportResponseByUser(Long userId, String nickanme, Long videoId, String videoTitle, String videoUrl, Long commentId,
                                String commentTitle, String commentContext, Long reportCount) {
        this.userId = userId;
        this.nickanme = nickanme;
        this.videoId = videoId;
        this.videoTitle = videoTitle;
        this.videoUrl = videoUrl;
        this.commentId = commentId;
        this.commentTitle = commentTitle;
        this.commentContext = commentContext;
        this.reportCount = reportCount;
    }
}
