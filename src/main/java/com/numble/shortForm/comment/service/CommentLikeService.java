package com.numble.shortForm.comment.service;

import com.numble.shortForm.comment.entity.Comment;
import com.numble.shortForm.comment.entity.CommentLike;
import com.numble.shortForm.comment.repository.CommentLikeRepository;
import com.numble.shortForm.comment.repository.CommentRepository;
import com.numble.shortForm.exception.CustomException;
import com.numble.shortForm.exception.ErrorCode;
import com.numble.shortForm.user.entity.Users;
import com.numble.shortForm.user.repository.UsersRepository;
import com.numble.shortForm.video.entity.Video;
import com.numble.shortForm.video.entity.VideoLike;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentLikeService {

    private final CommentLikeRepository commentLikeRepository;
    private final UsersRepository usersRepository;
    private final CommentRepository commentRepository;

    public boolean requestLikeComment(String userEmail, Long commentId) {

        Users users = usersRepository.findByEmail(userEmail).orElseThrow(()-> new CustomException(ErrorCode.NOT_FOUND_USER));
        Comment comment = commentRepository.findById(commentId).orElseThrow(() ->new CustomException(ErrorCode.BAD_REQUEST_PARAM));
        if (existsCommentLike(users, comment)) {
            commentLikeRepository.save(new CommentLike(users,comment));
            return true;
        }
        commentLikeRepository.deleteByUsersAndComment(users,comment);
        return false;
    }

    private boolean existsCommentLike(Users users, Comment comment) {
        return commentLikeRepository.findByUsersAndComment(users,comment).isEmpty();
    }
}
