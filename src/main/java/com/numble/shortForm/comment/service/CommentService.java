package com.numble.shortForm.comment.service;

import com.numble.shortForm.comment.dto.response.CommentNumberResponse;
import com.numble.shortForm.comment.dto.response.CommentResponse;
import com.numble.shortForm.comment.entity.Comment;
import com.numble.shortForm.comment.entity.CommentLike;
import com.numble.shortForm.comment.repository.CommentLikeRepository;
import com.numble.shortForm.comment.repository.CommentRepository;
import com.numble.shortForm.exception.CustomException;
import com.numble.shortForm.exception.ErrorCode;
import com.numble.shortForm.user.entity.Users;
import com.numble.shortForm.user.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final UsersRepository usersRepository;
    private final CommentLikeRepository commentLikeRepository;

    public void createComment(Comment comment, Long usersId){
        Users users = usersRepository.findById(usersId).orElseThrow(()->
             new CustomException(ErrorCode.NOT_FOUND_USER,"유저가 조회되지 않습니다.")
        );

        Comment newcoment = Comment.builder().
                context(comment.getContext()).
                title(comment.getTitle()).
                videoId(comment.getVideoId()).
                commentSeq(comment.getCommentSeq()).
                isBlock(false).
                users(users).build();

        commentRepository.save(newcoment);

        boolean createChek = false;


    }

    public List<CommentNumberResponse> testComment(Long videoId){
        List<CommentNumberResponse> responseLIst = commentRepository.videoCommentPage(videoId);

        return responseLIst;
    }

    public List<CommentResponse> videoComment(Long videoId){
        List<CommentResponse> responseList = commentRepository.commentPage(videoId);

        return responseList;
    }

    public List<CommentResponse> reComment(Long commentSeq){
        List<CommentResponse> responseList = commentRepository.recommentPage(commentSeq);

        return responseList;
    }

    public boolean updateComment(Comment comment, Long usersId){
        Users users = usersRepository.findById(usersId).orElseThrow(()-> new CustomException(ErrorCode.NOT_FOUND_USER,"유저가 조회되지 않습니다."));

        Comment bfComment = commentRepository.findById(comment.getId()).orElseThrow(()->
                new CustomException(ErrorCode.BAD_REQUEST_PARAM,"댓글 정보가 잘못되었습니다."));

        bfComment.setContext(comment.getContext());
        bfComment.setTitle(comment.getTitle());

        commentRepository.save(bfComment);
        //commentRepository.deleteById(comment.getId());

        return true;
    }

    public void deleteComment(Long commentId, Long usersId){
        Users users = usersRepository.findById(usersId).orElseThrow(()->
             new CustomException(ErrorCode.NOT_FOUND_USER,"유저가 조회되지 않습니다."));

        commentRepository.deleteById(commentId);
    }

    public boolean requestLikeComment(String UserEmail, Long commentId) {
        Users users = usersRepository.findByEmail(UserEmail).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new CustomException(ErrorCode.BAD_REQUEST_POST));

        if(existsCommentLike(users,comment)){
            commentLikeRepository.save(new CommentLike(users,comment));
            return  true;
        }
        commentLikeRepository.deleteByUsersAndComment(users,comment);
        return false;
    }

    private boolean existsCommentLike(Users users, Comment comment){
        return commentLikeRepository.findByUsersAndComment(users,comment).isEmpty();
    }

}
