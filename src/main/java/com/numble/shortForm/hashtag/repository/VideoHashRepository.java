package com.numble.shortForm.hashtag.repository;

import com.numble.shortForm.hashtag.entity.VideoHash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface VideoHashRepository extends JpaRepository<VideoHash,Long> {
    List<VideoHash> findAllByVideoId(Long videoId);

    List<VideoHash> findAllByVideoIdIn(List<Long> recordVideoList);

    List<VideoHash> findAllByHashTagIdIn(List<Long> tagids);
}
