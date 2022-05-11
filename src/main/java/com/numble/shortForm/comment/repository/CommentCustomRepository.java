package com.numble.shortForm.comment.repository;

import com.numble.shortForm.comment.dto.response.CommentNumberResponse;
import com.numble.shortForm.comment.dto.response.CommentNumberResponse;
import com.numble.shortForm.comment.dto.response.CommentResponse;

import java.util.List;

public interface CommentCustomRepository {
    List<CommentResponse> CommentPage(Long videoId);

    List<CommentResponse> recommentPage(Long commentSeq);

    List<CommentNumberResponse> videoCommentPage(Long videoId);
}
