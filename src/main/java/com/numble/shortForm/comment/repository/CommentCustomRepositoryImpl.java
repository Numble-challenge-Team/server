package com.numble.shortForm.comment.repository;

import com.numble.shortForm.comment.dto.response.OriginalComment;

import com.numble.shortForm.comment.dto.response.QOriginalComment;
import com.numble.shortForm.comment.entity.QComment;

import com.numble.shortForm.video.entity.QVideo;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.hibernate.sql.Alias;

import java.util.List;

import static com.numble.shortForm.comment.entity.QComment.comment;
import static com.numble.shortForm.user.entity.QUsers.users;
import static com.numble.shortForm.video.entity.QVideo.video;


@RequiredArgsConstructor
public class CommentCustomRepositoryImpl implements CommentCustomRepository{


    private final JPAQueryFactory queryFactory;


    @Override
    public List<OriginalComment> getComments(Long videoId) {


        QComment bb = new QComment("bb");

        List<OriginalComment> fetch = queryFactory.select(new QOriginalComment(
                        comment.id,
                        users.nickname,
                        comment.context,
                        comment.title,
                        users.profileImg.url,
                        comment.isBlock,
                        users.id,
                        JPAExpressions.select(bb.commentSeq.count())
                                .from(bb).where(bb.commentSeq.eq(comment.id)),
                        comment.commentLikes.size(),
                        comment.created_at
                )).from(comment)
                .leftJoin(comment.users,users)
                .orderBy(comment.created_at.asc())
                .where(comment.videoId.eq(videoId))
                .where(comment.commentSeq.eq(0L))
                .fetch();



        return fetch;
    }

    @Override
    public List<OriginalComment> getChildList(Long commentId) {
        List<OriginalComment> fetch = queryFactory.select(new QOriginalComment(
                        comment.id,
                        users.nickname,
                        comment.context,
                        comment.title,
                        users.profileImg.url,
                        comment.isBlock,
                        users.id,
                        comment.commentLikes.size(),
                        comment.created_at
                )).from(comment)
                .leftJoin(comment.users,users)
                .where(comment.commentSeq.eq(commentId))
                .orderBy(comment.id.asc())
                .fetch();

        return fetch;    }
}
