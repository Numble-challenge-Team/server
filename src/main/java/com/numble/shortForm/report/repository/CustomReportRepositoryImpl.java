package com.numble.shortForm.report.repository;


import com.numble.shortForm.report.dto.response.QReportResponseByVideo;
import com.numble.shortForm.report.dto.response.QreportResponseByUser;
import com.numble.shortForm.report.dto.response.ReportResponseByVideo;
import com.numble.shortForm.report.dto.response.reportResponseByUser;
import com.numble.shortForm.report.entity.QReport;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import java.util.List;

import static com.numble.shortForm.comment.entity.QComment.comment;
import static com.numble.shortForm.user.entity.QUsers.users;
import static com.numble.shortForm.video.entity.QVideo.video;
import static com.numble.shortForm.report.entity.QReport.report;

@RequiredArgsConstructor
public class CustomReportRepositoryImpl implements CustomReportRepository{

    private final JPAQueryFactory queryFactory;

    @Override
    public List<reportResponseByUser> reportListUserBy(int userId) {
        QReport areport = new QReport("areport");
        QReport breport = new QReport("breport");

        List<reportResponseByUser> fetch = queryFactory.select(new QreportResponseByUser(
                users.id,
                users.nickname,
                video.id,
                video.title,
                video.videoUrl,
                comment.id,
                comment.title,
                comment.context,
                ExpressionUtils.as(
                        JPAExpressions.select(breport.users.count()).from(breport)
                                .where(breport.users.eq(areport.users)),
                        "reportCount"
                )
        )).from(areport)
                .join( areport.users, users)
                .join(areport.users, video.users)
                .join(areport.users, comment.users)
                .fetch();

        fetch.forEach(Response -> {
            if(Response.getReportCount() > 5){
               Response.setFiveReport(true);
            } else {
                Response.setFiveReport(false);
            }
        });

        return fetch;
    }

    @Override
    public List<ReportResponseByVideo> reportListByVideo() {

        List<ReportResponseByVideo> fetch = queryFactory.select(new QReportResponseByVideo(
                video.id,
                video.title,
                video.videoUrl,
                video.view,
                video.created_at,
                video.isBlock,
                users.nickname,
                ExpressionUtils.as(JPAExpressions.select(report.count())
                        .from(report).where(report.videoId.eq(video.id)), "reportCount")
        )).from(video).join(video.users, users).where(video.users.id.eq(users.id)).fetch();

        return fetch;
    }
}
