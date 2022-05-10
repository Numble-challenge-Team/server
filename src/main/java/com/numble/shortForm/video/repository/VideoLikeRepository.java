package com.numble.shortForm.video.repository;

import com.numble.shortForm.user.entity.Users;
import com.numble.shortForm.video.entity.Video;
import com.numble.shortForm.video.entity.VideoLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VideoLikeRepository extends JpaRepository<VideoLike,Long> {
    Optional<VideoLike> findByUsersAndVideo(Users users, Video video);

    void deleteByUsersAndVideo(Users users, Video video);


    boolean existsByVideoAndUsers(Video video, Users users);
}
