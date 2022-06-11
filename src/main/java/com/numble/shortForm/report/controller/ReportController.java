package com.numble.shortForm.report.controller;

import com.numble.shortForm.config.security.UserLibrary;
import com.numble.shortForm.exception.CustomException;
import com.numble.shortForm.exception.ErrorCode;
import com.numble.shortForm.report.dto.request.ReportRequestDto;
import com.numble.shortForm.report.entity.Report;
import com.numble.shortForm.report.repository.ReportRepository;
import com.numble.shortForm.report.service.ReportService;
import com.numble.shortForm.user.repository.UsersRepository;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reports/")
@Api(tags = "신고 API")
public class ReportController {

//    private final ReportRepository reportRepository;
    private final ReportService reportService;
    private final UserLibrary userLibrary;

    @PostMapping("/create")
    public ResponseEntity createReport(@RequestBody ReportRequestDto reportRequestDto) {

        Long userId = userLibrary.retrieveUserId();

        reportService.createReport(userId,reportRequestDto);


        return ResponseEntity.ok().body("신고 완료");
    }


}
