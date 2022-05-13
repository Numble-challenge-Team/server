package com.numble.shortForm.comment.repository;

import com.numble.shortForm.comment.dto.response.OriginalComment;
import com.numble.shortForm.comment.dto.response.CommentResponse;

import java.util.List;

public interface CommentCustomRepository {

//    List<CommentResponse> commentPage(Long videoId);
//
//    List<CommentResponse> recommentPage(Long commentSeq);
//
//    List<OriginalComment> getCommentList(Long videoId);

    List<OriginalComment> getComments(Long videoId);

    List<OriginalComment> getChildList(Long commentId);
}
