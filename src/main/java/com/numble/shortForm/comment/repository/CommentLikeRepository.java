package com.numble.shortForm.comment.repository;

import com.numble.shortForm.comment.entity.Comment;
import com.numble.shortForm.comment.entity.CommentLike;
import com.numble.shortForm.user.entity.Users;
import com.numble.shortForm.video.entity.Video;
import com.numble.shortForm.video.entity.VideoLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentLikeRepository extends JpaRepository<CommentLike,Long> {
    Optional<CommentLike> findByUsersAndComment(Users users, Comment comment);

    void deleteByUsersAndComment(Users users, Comment comment);

    boolean existsByCommentAndUsers(Comment comment, Users users);
}
