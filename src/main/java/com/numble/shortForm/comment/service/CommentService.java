package com.numble.shortForm.comment.service;

import com.numble.shortForm.comment.dto.request.ChildCommentRequestDto;
import com.numble.shortForm.comment.dto.request.CommentRequestDto;
import com.numble.shortForm.comment.dto.response.OriginalComment;
import com.numble.shortForm.comment.entity.Comment;
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

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;
    private final UsersRepository usersRepository;
    private final CommentLikeRepository commentLikeRepository;

    public void createComment(CommentRequestDto dto,String userEmail){
        Users users = usersRepository.findByEmail(userEmail).orElseThrow(()->
                new CustomException(ErrorCode.NOT_FOUND_USER,"유저가 조회되지 않습니다.")
        );



        Comment comment = Comment.builder().
                context(dto.getContext()).
                title(dto.getTitle()).
                videoId(dto.getVideoId()).
                commentSeq(0L).
                isBlock(false).
                users(users).build();

        commentRepository.save(comment);
    }

    public List<OriginalComment> getCommentList(Long videoId,Long userId){
        List<OriginalComment> comments = commentRepository.getComments(videoId);

        if(userId !=0L){
            Users users = usersRepository.getById(userId);
            return checkLiked(users, comments);
        }
        return comments;

    }
    public List<OriginalComment> getChildList(Long commentId,Long userId) {

        List<OriginalComment> childList = commentRepository.getChildList(commentId);
        if(userId !=0L){
            Users users = usersRepository.getById(userId);
            return checkLiked(users, childList);
        }
        return childList;

    }

    private List<OriginalComment> checkLiked (Users users, List<OriginalComment> contents) {
        for (OriginalComment originalComment : contents) {

            //여기서 get사용 주의하자!!!!
            Comment comment = commentRepository.findById(originalComment.getId()).get();

            if (commentLikeRepository.existsByCommentAndUsers(comment, users)) {
                originalComment.setLiked(true);
                continue;
            }
            originalComment.setLiked(false);
        }
        return contents;
    }

    public void createChildComment(ChildCommentRequestDto dto, String userEmail) {

        Users users = usersRepository.findByEmail(userEmail).orElseThrow(()->
                new CustomException(ErrorCode.NOT_FOUND_USER,"유저가 조회되지 않습니다.")
        );

        Comment comment = Comment.builder().
                context(dto.getContext()).
                title(dto.getTitle()).
                videoId(dto.getVideoId()).
                commentSeq(dto.getCommentId()).
                isBlock(false).
                users(users).build();

        commentRepository.save(comment);

    }


    public void updateComment(Users user, ChildCommentRequestDto dto) {
        Comment comment = commentRepository.findById(dto.getCommentId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_COMMENT));

        if(!comment.getUsers().getId().equals(user.getId()))
            throw new CustomException(ErrorCode.NOT_OWNER);

        comment.updateComment(dto.getTitle(), dto.getContext());

        commentRepository.save(comment);

    }

    public boolean deleteComment(Long commentId,Long usersId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_COMMENT,"해당 댓글이 존재하지 않습니다."));

        if (comment.getUsers().getId() != usersId)
            throw new CustomException(ErrorCode.NOT_OWNER,"해당 댓글의 작성자가 아닙니다.");



            commentRepository.deleteAllByCommentSeq(commentId);
            commentRepository.delete(comment);

            if(commentRepository.existsById(commentId))
                return false;

            return true;
    }

}
