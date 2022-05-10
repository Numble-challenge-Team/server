package com.numble.shortForm.comment.repository;

import com.numble.shortForm.comment.dto.response.CommentNumberResponse;
import com.numble.shortForm.comment.dto.response.CommentResponse;
import com.numble.shortForm.comment.dto.response.QCommentNumberResponse;
import com.numble.shortForm.comment.dto.response.QCommentResponse;
import com.numble.shortForm.comment.entity.QComment;
import com.numble.shortForm.user.entity.QUsers;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.numble.shortForm.comment.entity.QComment.comment;
import static com.numble.shortForm.user.entity.QUsers.users;


@RequiredArgsConstructor
public class CommentCustomRepositoryImpl implements CommentCustomRepository{


    private final JPAQueryFactory queryFactory;


    @Override
    public List<CommentResponse> commentPage(Long videoId) {
        List<CommentResponse> fetch = queryFactory.select( new QCommentResponse(
                        comment.id,
                        users.nickname,
                        comment.context,
                        comment.title,
                        comment.isBlock,
                        users.id,
                        comment.commentSeq,
                        comment.videoId
                )).from(comment).where(comment.videoId.eq(videoId))
                .innerJoin(comment.users, users)
                .orderBy(comment.created_at.desc())
                .fetch();

        return fetch;
    }

    public List<CommentResponse> recommentPage(Long commentSeq) {
        List<CommentResponse> fetch = queryFactory.select( new QCommentResponse(
                comment.id,
                users.nickname,
                comment.context,
                comment.title,
                comment.isBlock,
                users.id,
                comment.commentSeq,
                comment.videoId
        )).from(comment).where(comment.commentSeq.eq(commentSeq)).fetch();

        return fetch;
    }

    @Override
    public List<CommentNumberResponse> videoCommentPage(Long videoId){


        QComment acomment = new QComment("acomment");
        QComment bcomment = new QComment("bcomment");


        List<CommentNumberResponse> fetch = queryFactory.select( new QCommentNumberResponse(
                acomment.id,
                users.nickname,
                acomment.context,
                acomment.title,
                acomment.isBlock,
                users.id,
                acomment.commentSeq,
                acomment.videoId,
                ExpressionUtils.as(
                        JPAExpressions.select(bcomment.commentSeq.count())
                                .from(bcomment).where(bcomment.commentSeq.eq(acomment.id)),
                        "commentCount"
                )
        )).from(acomment).where(acomment.videoId.eq(videoId)).fetch();

        fetch.forEach( CommentNumberResponse -> {
                    if(CommentNumberResponse.getCommentCount() == 0){
                        CommentNumberResponse.setReComment(false);
                    } else{
                        CommentNumberResponse.setReComment(true);
                    }
                }
        );

        return fetch;
    }
}
