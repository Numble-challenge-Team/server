package com.numble.shortForm.video.repository;

import com.numble.shortForm.video.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoRepository extends JpaRepository<Video,Long> ,VideoCustomRepository{
}
