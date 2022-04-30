package com.numble.shortForm.video.repository;

import com.numble.shortForm.video.dto.response.VideoResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface VideoCustomRepository {
    Page<VideoResponseDto> retrieveAll(Pageable pageable);
}
