package com.numble.shortForm.comment.repository;

import com.numble.shortForm.comment.dto.response.*;
import com.numble.shortForm.comment.entity.QComment;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.numble.shortForm.comment.entity.QComment.comment;
import static com.numble.shortForm.user.entity.QUsers.users;
import static com.numble.shortForm.comment.entity.QCommentLike.commentLike;


@RequiredArgsConstructor
public class CommentCustomRepositoryImpl implements CommentCustomRepository{

    private final JPAQueryFactory queryFactory;

    //public CommentCustomRepositoryImpl() {queryFactory = null;}


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
                comment.videoId,
                comment.created_at,
                ExpressionUtils.as(JPAExpressions.select(commentLike.comment.id.count()).from(commentLike).
                        where(comment.id.eq(commentLike.comment.id))
                        ,"likeCount"),
                users.profileImg.url
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
                comment.videoId,
                comment.created_at,
                ExpressionUtils.as(JPAExpressions.select(commentLike.comment.id.count()).from(commentLike)
                        .where(comment.id.eq(commentLike.comment.id)),"likeCount"),
                users.profileImg.url
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
                ),
                ExpressionUtils.as(
                        JPAExpressions.select(commentLike.comment.id.count())
                                .from(commentLike).where(acomment.id.eq(commentLike.comment.id)),
                        "likeCount"
                ),
                acomment.created_at,
                acomment.users.profileImg.url
        )).from(acomment).where(acomment.videoId.eq(videoId)).fetch();

        fetch.forEach( commentNumberResponse -> {
                if(commentNumberResponse.getCommentCount() == 0){
                    commentNumberResponse.setReComment(false);
                } else{
                    commentNumberResponse.setReComment(true);
                }
            }
        );

        return fetch;
    }
}
