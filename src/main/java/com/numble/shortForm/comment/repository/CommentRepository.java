package com.numble.shortForm.comment.repository;

import com.numble.shortForm.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment,Long> ,CommentCustomRepository{

    @Query(value = "DELETE  FROM Comment c where c.commentSeq =:commentId" )
    List<Long> deletChild(Long commentId);

    void deleteAllByCommentSeq(Long commentId);
}
