package com.numble.shortForm.comment.repository;

import com.numble.shortForm.comment.entity.Comment;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Primary
@Repository
public interface CommentRepository extends JpaRepository<Comment,Long>, CommentCustomRepository{
}
