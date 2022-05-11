package com.numble.shortForm.report.service;

import com.numble.shortForm.report.entity.Report;
import com.numble.shortForm.report.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;

    public void CreateReport(Report report){
        Report newReport = Report.builder().users(report.getUsers()).videoID(report.getVideoID())
                .commentID(report.getCommentID()).build();

    }
}
