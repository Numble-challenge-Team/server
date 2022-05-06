package com.numble.shortForm.video.repository;

import com.numble.shortForm.request.PageDto;
import com.numble.shortForm.video.dto.response.VideoResponseDto;
import com.numble.shortForm.video.entity.VideoLike;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface VideoCustomRepository {
    Page<VideoResponseDto> retrieveAll(Pageable pageable);


    VideoResponseDto retrieveDetail(Long videoId);

    Page<VideoResponseDto> retrieveMyVideo(String userEmail, Pageable pageable);

    List<VideoResponseDto> getVideoByTag(Long videoId);

    List<VideoResponseDto> searchVideoQuery(String query,Pageable pageable);

    Page<VideoResponseDto> retrieveMainVideo(Pageable pageable);

    Page<VideoResponseDto> retrieveMainVideoNotLogin(Pageable pageable);
}
