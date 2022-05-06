package com.numble.shortForm.video.repository;

import com.numble.shortForm.user.entity.Users;
import com.numble.shortForm.video.entity.Video;
import com.numble.shortForm.video.entity.VideoLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface VideoRepository extends JpaRepository<Video,Long> ,VideoCustomRepository{

    @Modifying
    @Query("update Video v set v.view = v.view+1 where v.id =:videoId")
    int updateView(Long videoId);



}
