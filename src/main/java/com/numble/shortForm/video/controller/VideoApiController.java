package com.numble.shortForm.video.controller;

import com.numble.shortForm.exception.CustomException;
import com.numble.shortForm.exception.ErrorCode;
import com.numble.shortForm.exception.ErrorResponse;
import com.numble.shortForm.exception.ServerErrorResponse;
import com.numble.shortForm.request.PageDto;
import com.numble.shortForm.response.Response;
import com.numble.shortForm.security.AuthenticationFacade;
import com.numble.shortForm.user.entity.Users;
import com.numble.shortForm.user.repository.UsersRepository;
import com.numble.shortForm.video.dto.request.EmbeddedVideoRequestDto;
import com.numble.shortForm.video.dto.request.NormalVideoRequestDto;
import com.numble.shortForm.video.dto.request.UpdateVideoDto;
import com.numble.shortForm.video.dto.request.VideoCode;
import com.numble.shortForm.video.dto.response.IsLikeResponse;
import com.numble.shortForm.video.dto.response.Result;
import com.numble.shortForm.video.dto.response.VideoDetailResponseDto;
import com.numble.shortForm.video.dto.response.VideoResponseDto;
import com.numble.shortForm.video.entity.VideoType;
import com.numble.shortForm.video.service.VideoService;
import com.numble.shortForm.video.vimeo.Vimeo;
import com.numble.shortForm.video.vimeo.VimeoException;
import com.numble.shortForm.video.vimeo.VimeoLogic;
import com.numble.shortForm.video.vimeo.VimeoResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/v1/videos/")
@RequiredArgsConstructor
@Slf4j
@Api(tags = "비디오 API")
public class VideoApiController {

    private final VideoService videoService;
    private final AuthenticationFacade authenticationFacade;
    private final UsersRepository usersRepository;
    private final VimeoLogic vimeoLogic;

    @Value("${vimeo.token}")
    private String vimeoToken;


//    @GetMapping("/check")
//    public ResponseEntity checkVideo(@Param("url") String url,@Param("keyword") String keyword) throws IOException {
//
//        Vimeo vimeo = new Vimeo(vimeoToken);
//        VimeoResponse videoInfo = vimeo.getVideoInfo(url);
//        JSONObject json = videoInfo.getJson();
//        JSONObject obj =(JSONObject) json.get("embed");
//
//        System.out.println(obj.get("html"));
//        System.out.println(obj);
//
//
////        Iterator<String> keys = json.keys();
////
////        ArrayList<String> keyList = new ArrayList<>();
////        while(keys.hasNext()) {
////            String key = keys.next().toString();
////            Object o = json.get(key);
////            System.out.println(key+": "+ o);
////            keyList.add(key);
////        }
//
//
//
//        return ResponseEntity.ok().body(obj);
//    }



    @ApiOperation(value = "일반동영상 업로드", notes = "")
    @PostMapping("/upload/normal")
    public ResponseEntity<?> uploadNormalVideo(@ModelAttribute NormalVideoRequestDto normalVideoRequestDto) throws IOException, VimeoException, InterruptedException {

        MultipartFile video = normalVideoRequestDto.getVideo();
        //비디오 파일 저장
        String videoEndpoint = vimeoLogic.uploadNormalVideo(video);

        Long userId = retrieveUserId();
        if (userId == 0L) {
            throw new CustomException(ErrorCode.NOT_FOUND_USER,"유효한 유저가 아닙니다.");
        }

        videoService.uploadDirectVideo(videoEndpoint,normalVideoRequestDto,userId);

        return ResponseEntity.ok("저장 완료");

    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @ApiOperation(value = "임베디드 동영상 업로드", notes = "(Token 필요)<big>임베디드 동영상 업로드</big>")
    @ApiResponses({
            @ApiResponse(code = 200, message = "동영상 생성 완료", response = Response.class),
            @ApiResponse(code = 403, message = "권한 없음", response = ErrorResponse.class, responseContainer = "List"),
            @ApiResponse(code = 404, message = "유저 NOT Found 오류", response = ErrorResponse.class, responseContainer = "List"),
            @ApiResponse(code = 500, message = "서버 에러", response = ServerErrorResponse.class, responseContainer = "List")
    })
    @PostMapping("/upload/embedded")
    public ResponseEntity<?> uploadEmbeddedVideo(@ModelAttribute EmbeddedVideoRequestDto embeddedVideoRequestDto) throws IOException {

        String userEmail = authenticationFacade.getAuthentication().getName();

        Users users = usersRepository.findByEmail(userEmail).orElseThrow(() ->
                new CustomException(ErrorCode.NOT_FOUND_USER, "해당 유저 토큰에 해당하는 정보를 찾을수가 없습니다."));

        videoService.uploadEmbeddedVideo(embeddedVideoRequestDto, users.getId());

        return ResponseEntity.ok().body("ok");
    }

    @GetMapping("/main")
    public Result mainVideoList( Pageable pageable) {

        Long userId = retrieveUserId();
        if (userId == 0L) {
            log.info("로그인 안됌");
            return videoService.retrieveMainVideoListNotLogin(pageable);
        }

        return videoService.retrieveMainVideoList(pageable,userId);

    }


    @ApiOperation(value = "비디오 상세페이지", notes = "비디오 Id 값을 기준으로 조회")
    @GetMapping("/retrieve/{videoId}")
    public VideoDetailResponseDto retrieveVideoDetail(@PathVariable(name = "videoId") Long videoId) throws IOException {
//        comment 개발후 작성
        String ip = getIp();
        Long userId = retrieveUserId();

        if (userId == 0L) {
            return videoService.retrieveDetailNotLogin(videoId,ip);
        }

        return videoService.retrieveDetail(videoId,ip,userId);

    }

    @ApiOperation(value = "로그인 유저의 모든 비디오 조회", notes = "(Token 필요)테스트하실때 확인하실 동영상 리스트 조회, size는 parameter로")
    @GetMapping("/retrieve/myVideo")
    public Result retrieveMyVideo(Pageable pageable) {
        String userEmail = authenticationFacade.getAuthentication().getName();


        return videoService.retrieveMyVideo(userEmail,pageable);
    }


    @ApiOperation(value = "비디오 좋아요버튼", notes = "(Token 필요)좋아요 생성,혹은 제거 메시지가 표시됌")
    @PostMapping("/like/{videoId}")
    public ResponseEntity requestLikeVideo(@PathVariable(name = "videoId")Long videoId ) {
        String userEmail = authenticationFacade.getAuthentication().getName();

        boolean bol = videoService.requestLikeVideo(userEmail, videoId);


        return ResponseEntity.ok().body(new IsLikeResponse(bol));
    }

    @ApiOperation(value = "비디오 검색", notes = "검색기능, 현재 제목과, description 조회(해시태그 포함예정)")
    @GetMapping("/search")
    public Result searchVideo(@RequestParam String query,Pageable pageable) {

        return videoService.searchVideoQuery(query,pageable,retrieveUserId());

    }


    @ApiOperation(value = "관심 동영상 리스트 반환", notes = "유저의 시청기록을 기반으로 관심영상 반환,아직 개발중")
    @GetMapping("/concernVideo/{videoId}")
    public Page<VideoResponseDto> getConcernVideo(Pageable pageable,@PathVariable("videoId")Long videoId ) {
            return videoService.retrieveConcernVideosNotLogin(pageable,videoId,retrieveUserId());
    }

    @ApiOperation(value = "유저가 좋아요한 비디오 리스트", notes = "유저가 좋아요한 비디오의 리시트, 토큰값 필요")
    @GetMapping("/likesVideos")
    public Result getLikesVideos(Pageable pageable) {
        Long userId = retrieveUserId();

        return videoService.retrieveLikesVideos(pageable,userId);
    }


    @PutMapping("/update")
    public ResponseEntity updateVideo(UpdateVideoDto updateVideoDto) throws IOException {

        if (!retrieveUserId().equals(updateVideoDto.getUsersId())) {
            throw new CustomException(ErrorCode.ACCESS_DENIED,"비디오에 접근 권한이 없습니다.");
        }

        if (updateVideoDto.getType().equals(VideoType.upload)) {
            videoService.updateUploadVideo(updateVideoDto);
            return ResponseEntity.ok().body("변경완료");
        }

         videoService.updateEmbeddedVideo(updateVideoDto);
        return ResponseEntity.ok().body("변경완료");
    }

    // ip 조회
     private String getIp() {
        HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String ip = req.getHeader("X-FORWARDED-FOR");
        if (ip == null)
            ip = req.getRemoteAddr();
        return ip;
    }
    // userid 조회
    private Long retrieveUserId() {
        String userEmail = authenticationFacade.getAuthentication().getName();
      if(userEmail.equals("anonymousUser")){
          return 0L;
      }
            Users users = usersRepository.findByEmail(userEmail).orElseThrow(()->{
                throw new CustomException(ErrorCode.NOT_FOUND_USER,"유저가 조회되지 않습니다.");
            });
            return users.getId();
    }
}
