package com.numble.shortForm.report.controller;

import com.numble.shortForm.exception.CustomException;
import com.numble.shortForm.exception.ErrorCode;
import com.numble.shortForm.exception.ErrorResponse;
import com.numble.shortForm.report.dto.response.ReportResponseByVideo;
import com.numble.shortForm.report.entity.Report;
import com.numble.shortForm.report.service.ReportService;
import com.numble.shortForm.response.Response;
import com.numble.shortForm.security.AuthenticationFacade;
import com.numble.shortForm.user.entity.Users;
import com.numble.shortForm.user.repository.UsersRepository;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(value = "신고관련 api")
@RequestMapping(value = "/api/v1/reports")
@RestController
public class ReportApiController {

    @Autowired
    AuthenticationFacade authenticationFacade;

    @Autowired
    UsersRepository usersRepository;

    @Autowired
    ReportService reportService;

    @ApiOperation(value = "신고작성", notes = "<big>신고에 성공하면, ok 반환</big>")
    @ApiImplicitParam(name = "Report", value = "신고를 작성하기위한 json")
    @ApiResponses({
            @ApiResponse(code = 200, message = "ok", response = Response.class),
            @ApiResponse(code =404, message = "해당유저를 찾을 수 없습니다", response = ErrorResponse.class),
            @ApiResponse(code = 500, message = "서버내부에러", response = ErrorResponse.class)
    })
    @PostMapping(value = "/report")
    public ResponseEntity<?> createReport(@RequestBody Report report){

        String userEmail = authenticationFacade.getAuthentication().getName();

        Users user = usersRepository.findByEmail(userEmail).orElseThrow(()->
                new CustomException(ErrorCode.NOT_FOUND_USER, "해당유저에 해당하는 토큰을 찾을 수 없습니."));

        reportService.CreateReport(report,user);
        return ResponseEntity.ok().body("ok");
    }

    @ApiOperation(value = "신고리스트 출력", notes = "관리자 권한만 리스타값 반환")
    @ApiImplicitParam(name = "bearer token", value = "accessToken 값")
    @ApiResponse(code = 200, message = "json list형태", response = ReportResponseByVideo.class)
    @GetMapping(value = "/list")
    public List<ReportResponseByVideo> reportResponseByVideoList(){
        String userEmail = authenticationFacade.getAuthentication().getName();

        Users users = usersRepository.findByEmail((userEmail)).orElseThrow(() ->
                new CustomException(ErrorCode.NOT_FOUND_USER, "해당유저에 해당하는 토큰을 찾을 수 없습니."));
        List<ReportResponseByVideo> reponseList = reportService.reportResponseByVideoList();

        return reponseList;
    }


}
