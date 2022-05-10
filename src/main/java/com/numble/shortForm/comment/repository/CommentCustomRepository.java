package com.numble.shortForm.comment.repository;

import com.numble.shortForm.comment.dto.response.commentNumberResponse;
import com.numble.shortForm.comment.dto.response.commentResponse;

import java.util.List;

public interface CommentCustomRepository {
    List<commentResponse> commentPage(Long videoId);

    List<commentResponse> recommentPage(Long commentSeq);

    List<commentNumberResponse> videoCommentPage(Long videoId);
}
