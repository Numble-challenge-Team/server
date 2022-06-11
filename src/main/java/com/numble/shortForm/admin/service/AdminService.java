package com.numble.shortForm.admin.service;

import com.numble.shortForm.exception.CustomException;
import com.numble.shortForm.exception.ErrorCode;
import com.numble.shortForm.report.repository.ReportRepository;
import com.numble.shortForm.user.service.UserService;
import com.numble.shortForm.video.entity.Video;
import com.numble.shortForm.video.repository.VideoRepository;
import com.numble.shortForm.video.service.VideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminService {

    private final UserService userService;
    private final VideoRepository videoRepository;
    private final ReportRepository reportRepository;

    public ResponseEntity<?> blockVideo(Long videoId,boolean bol) {
        Video video =videoRepository.findById(videoId)
                .orElseThrow(()-> new CustomException(ErrorCode.NOT_FOUND_VIDEO));

        video.changeBlock(true);
        return ResponseEntity.ok().body("true");
    }


    public ResponseEntity<?> getVideoReports(Long videoId) {

        Video video =videoRepository.findById(videoId)
                .orElseThrow(()-> new CustomException(ErrorCode.NOT_FOUND_VIDEO));
        return ResponseEntity.ok().body(video.getReports());

    }
}
