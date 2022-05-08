package com.numble.shortForm.video.repository;

import com.numble.shortForm.video.dto.response.Result;
import com.numble.shortForm.video.dto.response.VideoResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface VideoCustomRepository {
    Page<VideoResponseDto> retrieveAll(Pageable pageable);


    VideoResponseDto retrieveDetail(Long videoId,Long userId);

    Result retrieveMyVideo(String userEmail, Pageable pageable);

    List<VideoResponseDto> getVideoByTag(Long videoId);

    Page<VideoResponseDto> searchVideoQuery(String query,Pageable pageable,Long userId);

    Page<VideoResponseDto> retrieveMainVideo(Pageable pageable,Long userId);

    Page<VideoResponseDto> retrieveMainVideoNotLogin(Pageable pageable);

    Page<VideoResponseDto> retrieveConcernVideo(List<Long> videoids,Long videoId,Pageable pageable,Long userId);
}
