package com.numble.shortForm.report.service;

import com.numble.shortForm.report.dto.response.ReportResponseByVideo;
import com.numble.shortForm.report.entity.Report;
import com.numble.shortForm.report.repository.ReportRepository;
import com.numble.shortForm.user.entity.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;

    public void CreateReport(Report report, Users users){


        Report newReport = Report.builder().users(users).videoId(report.getVideoId())
                .commentID(report.getCommentID()).build();
        //System.out.println(newReport.toString());
        reportRepository.save(newReport);

    }

    public List<ReportResponseByVideo> reportResponseByVideoList(){

        List<ReportResponseByVideo> responseList = reportRepository.reportListByVideo();

        return responseList;
    }
}
