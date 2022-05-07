package com.numble.shortForm.video.repository;

import com.numble.shortForm.video.entity.RecordVideo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecordVideoRepository extends JpaRepository<RecordVideo,Long> {

    Page<RecordVideo> findAllByVideoIdAndUserId(Long videoId, Long userId, Pageable pageable);

    Page<RecordVideo> findAllByVideo_IdAndUser_Id(Long videoId, Long userId, Pageable pageable);
}
