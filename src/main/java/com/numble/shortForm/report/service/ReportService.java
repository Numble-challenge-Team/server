package com.numble.shortForm.report.service;

import com.numble.shortForm.exception.CustomException;
import com.numble.shortForm.exception.ErrorCode;
import com.numble.shortForm.report.dto.request.ReportRequestDto;
import com.numble.shortForm.report.entity.Report;
import com.numble.shortForm.report.repository.ReportRepository;
import com.numble.shortForm.user.entity.Users;
import com.numble.shortForm.user.repository.UsersRepository;
import com.numble.shortForm.video.entity.Video;
import com.numble.shortForm.video.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;

@Service
@Transactional
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final UsersRepository usersRepository;
    private final VideoRepository videoRepository;

    public void createReport(Long userId, ReportRequestDto reportRequestDto) {

        Long videoId = reportRequestDto.getVideoId();


        if (reportRepository.existsByVideoIdAndUsersId(videoId, userId))
            throw new CustomException(ErrorCode.EXIST_REPORT);

        Users users = usersRepository.findById(userId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_USER));

        Video video = videoRepository.findById(videoId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_VIDEO));

        reportRepository.save(new Report(users, video, reportRequestDto.getContext()));

    }
}
