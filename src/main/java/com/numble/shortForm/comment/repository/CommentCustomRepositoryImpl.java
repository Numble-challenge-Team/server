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


//    @Override
//    public List<CommentResponse> commentPage(Long videoId) {
//        List<CommentResponse> fetch = queryFactory.select( new QCommentResponse(
//                        comment.id,
//                        users.nickname,
//                        comment.context,
//                        comment.title,
//                        comment.isBlock,
//                        users.id,
//                        comment.commentSeq,
//                        comment.videoId
//                )).from(comment).where(comment.videoId.eq(videoId))
//                .innerJoin(comment.users, users)
//                .orderBy(comment.created_at.desc())
//                .fetch();
//
//        return fetch;
//    }
//
//    public List<CommentResponse> recommentPage(Long commentSeq) {
//        List<CommentResponse> fetch = queryFactory.select( new QCommentResponse(
//                comment.id,
//                users.nickname,
//                comment.context,
//                comment.title,
//                comment.isBlock,
//                users.id,
//                comment.commentSeq,
//                comment.videoId
//        )).from(comment).where(comment.commentSeq.eq(commentSeq)).fetch();
//
//        return fetch;
//    }
//
//    @Override
//    public List<OriginalComment> getCommentList(Long videoId){
//
//
//        QComment acomment = new QComment("acomment");
//        QComment bcomment = new QComment("bcomment");
//
//
//        List<OriginalComment> fetch = queryFactory.select( new QCommentNumberResponse(
//                acomment.id,
//                users.nickname,
//                acomment.context,
//                acomment.title,
//                acomment.isBlock,
//                users.id,
//                acomment.commentSeq,
//                acomment.videoId,
//                ExpressionUtils.as(
//                        JPAExpressions.select(bcomment.commentSeq.count())
//                                .from(bcomment).where(bcomment.commentSeq.eq(acomment.id)),
//                        "commentCount"
//                )
//        )).from(acomment).where(acomment.videoId.eq(videoId)).fetch();
//
//        fetch.forEach( CommentNumberResponse -> {
//                    if(CommentNumberResponse.getCommentCount() == 0){
//                        CommentNumberResponse.setReComment(false);
//                    } else{
//                        CommentNumberResponse.setReComment(true);
//                    }
//                }
//        );
//
//        return fetch;
//    }

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
                .orderBy(comment.id.desc())
                .fetch();

        return fetch;    }
}
