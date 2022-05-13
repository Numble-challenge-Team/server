package com.numble.shortForm.report.controller;

import com.numble.shortForm.config.security.UserLibrary;
import com.numble.shortForm.exception.CustomException;
import com.numble.shortForm.exception.ErrorCode;
import com.numble.shortForm.report.dto.request.ReportRequestDto;
import com.numble.shortForm.report.entity.Report;
import com.numble.shortForm.report.repository.ReportRepository;
import com.numble.shortForm.report.service.ReportService;
import com.numble.shortForm.user.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reports/")
public class ReportController {

//    private final ReportRepository reportRepository;
    private final ReportService reportService;
    private final UserLibrary userLibrary;

    @PostMapping("/create")
    public ResponseEntity createReport(ReportRequestDto reportRequestDto) {

        Long userId = userLibrary.retrieveUserId();

        reportService.createReport(userId,reportRequestDto);


        return ResponseEntity.ok().body("신고 완료");
    }


}
