package com.numble.shortForm.video.controller;

import com.numble.shortForm.config.security.UserLibrary;
import com.numble.shortForm.exception.CustomException;
import com.numble.shortForm.exception.ErrorCode;
import com.numble.shortForm.exception.ErrorResponse;
import com.numble.shortForm.exception.ServerErrorResponse;
import com.numble.shortForm.response.Response;
import com.numble.shortForm.config.security.AuthenticationFacade;
import com.numble.shortForm.upload.S3Uploader;
import com.numble.shortForm.user.entity.Users;
import com.numble.shortForm.user.jwt.JwtTokenProvider;
import com.numble.shortForm.user.repository.UsersRepository;
import com.numble.shortForm.video.dto.request.EmbeddedVideoRequestDto;
import com.numble.shortForm.video.dto.request.NormalVideoRequestDto;
import com.numble.shortForm.video.dto.request.UpdateVideoDto;
import com.numble.shortForm.video.dto.response.IsLikeResponse;
import com.numble.shortForm.video.dto.response.Result;
import com.numble.shortForm.video.dto.response.VideoDetailResponseDto;
import com.numble.shortForm.video.dto.response.VideoResponseDto;
import com.numble.shortForm.video.entity.VideoType;
import com.numble.shortForm.video.service.VideoService;
import com.numble.shortForm.video.vimeo.VimeoException;
import com.numble.shortForm.video.vimeo.VimeoLogic;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
@RequestMapping("/api/v1/videos/")
@RequiredArgsConstructor
@Slf4j
@Api(tags = "????????? API")
public class VideoApiController {

    private final VideoService videoService;
    private final AuthenticationFacade authenticationFacade;
    private final UsersRepository usersRepository;
    private final VimeoLogic vimeoLogic;
    private final UserLibrary userLibrary;
    private static final String IMAGE_TYPE ="thumbnail";
    private final JwtTokenProvider jwtTokenProvider;
    private static final String AUTHORIZATION_HEADER ="Authorization";
    private static final String BEARER_TYPE  ="Bearer";


    @ApiOperation(value = "??????????????? ?????????", notes = "")
    @PostMapping("/upload/normal")
    public ResponseEntity<?> uploadNormalVideo(@ModelAttribute NormalVideoRequestDto normalVideoRequestDto) throws IOException, VimeoException, InterruptedException {

        MultipartFile video = normalVideoRequestDto.getVideo();
        //????????? ?????? ??????
        String videoEndpoint = vimeoLogic.uploadNormalVideo(video);

        Long userId = userLibrary.retrieveUserId();
        if (userId == 0L) {
            throw new CustomException(ErrorCode.NOT_FOUND_USER,"????????? ????????? ????????????.");
        }

        videoService.uploadDirectVideo(videoEndpoint,normalVideoRequestDto,userId);

        return ResponseEntity.ok("?????? ??????");

    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @ApiOperation(value = "???????????? ????????? ?????????", notes = "(Token ??????)<big>???????????? ????????? ?????????</big>")
    @ApiResponses({
            @ApiResponse(code = 200, message = "????????? ?????? ??????", response = Response.class),
            @ApiResponse(code = 403, message = "?????? ??????", response = ErrorResponse.class, responseContainer = "List"),
            @ApiResponse(code = 404, message = "?????? NOT Found ??????", response = ErrorResponse.class, responseContainer = "List"),
            @ApiResponse(code = 500, message = "?????? ??????", response = ServerErrorResponse.class, responseContainer = "List")
    })
    @PostMapping("/upload/embedded")
    public ResponseEntity<?> uploadEmbeddedVideo(@ModelAttribute EmbeddedVideoRequestDto embeddedVideoRequestDto) throws IOException {

        log.info("embedded dto {}",embeddedVideoRequestDto);
        log.info("thumb {}",embeddedVideoRequestDto.getThumbnail().isEmpty());

        String userEmail = authenticationFacade.getAuthentication().getName();

        Users users = usersRepository.findByEmail(userEmail).orElseThrow(() ->
                new CustomException(ErrorCode.NOT_FOUND_USER, "?????? ?????? ????????? ???????????? ????????? ???????????? ????????????."));

        videoService.uploadEmbeddedVideo(embeddedVideoRequestDto, users.getId());

        return ResponseEntity.ok().body("ok");
    }

    @GetMapping("/main")
    public Result mainVideoList(Pageable pageable,HttpServletRequest request) {
        // header?????? guest ?????? ??????

        checkToken(request);

        Long userId=userLibrary.retrieveUserId();

        if (userId == 0L) {
            log.info("????????? ??????");
            return videoService.retrieveMainVideoListNotLogin(pageable);
        }

        return videoService.retrieveMainVideoList(pageable,userId);

    }


    @ApiOperation(value = "????????? ???????????????", notes = "????????? Id ?????? ???????????? ??????")
    @GetMapping("/retrieve/{videoId}")
    public VideoDetailResponseDto retrieveVideoDetail(@PathVariable(name = "videoId") Long videoId,HttpServletRequest request) throws IOException {

        checkToken(request);

        String ip = getIp();
        Long userId = userLibrary.retrieveUserId();
        if (userId == 0L) {
            return videoService.retrieveDetailNotLogin(videoId,ip);
        }
        log.info("???????????? detail");
        return videoService.retrieveDetail(videoId,ip,userId);

    }

    @ApiOperation(value = "????????? ????????? ?????? ????????? ??????", notes = "(Token ??????)?????????????????? ???????????? ????????? ????????? ??????, size??? parameter???")
    @GetMapping("/retrieve/myVideo")
    public Result retrieveMyVideo(Pageable pageable) {
        String userEmail = authenticationFacade.getAuthentication().getName();


        return videoService.retrieveMyVideo(userEmail,pageable);
    }


    @ApiOperation(value = "????????? ???????????????", notes = "(Token ??????)????????? ??????,?????? ?????? ???????????? ?????????")
    @PostMapping("/like/{videoId}")
    public ResponseEntity requestLikeVideo(@PathVariable(name = "videoId")Long videoId ) {
        String userEmail = authenticationFacade.getAuthentication().getName();

        boolean bol = videoService.requestLikeVideo(userEmail, videoId);


        return ResponseEntity.ok().body(new IsLikeResponse(bol));
    }

    @ApiOperation(value = "????????? ??????", notes = "????????????, ?????? ?????????, description ??????(???????????? ????????????)")
    @GetMapping("/search")
    public Result searchVideo(@RequestParam String query,Pageable pageable) {

        return videoService.searchVideoQuery(query,pageable,userLibrary.retrieveUserId());

    }


    @ApiOperation(value = "?????? ????????? ????????? ??????", notes = "????????? ??????????????? ???????????? ???????????? ??????,?????? ?????????")
    @GetMapping("/concernVideo/{videoId}")
    public Page<VideoResponseDto> getConcernVideo(Pageable pageable,@PathVariable("videoId")Long videoId ) {
            return videoService.retrieveConcernVideosNotLogin(pageable,videoId,userLibrary.retrieveUserId());
    }




    @ApiOperation(value = "????????? ???????????? ????????? ?????????", notes = "????????? ???????????? ???????????? ?????????, ????????? ??????")
    @GetMapping("/likesVideos")
    public Result getLikesVideos(Pageable pageable) {
        Long userId = userLibrary.retrieveUserId();

        return videoService.retrieveLikesVideos(pageable,userId);
    }

//    @ApiOperation(value = "thumbnail ????????? ????????????", notes = "thumbnail ????????? ????????????")
//    @GetMapping("/retrieveImg/{filename}")
//    public ResponseEntity<Resource> updateForm(@PathVariable("filename")String filename, HttpServletRequest request, HttpServletResponse response) throws IOException {
//
//
//        Resource resource = s3Uploader.getObject(filename,IMAGE_TYPE);
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);
//        headers.add("Content-type","image/png");
//        return new ResponseEntity<>(resource, headers, HttpStatus.OK);
//
//    }

    @GetMapping("/record")
    public ResponseEntity retrieveRecord(Pageable pageable) {
//        Long userId = userLibrary.retrieveUserId();
//        if (userId == 0L) {
//            throw new RuntimeException();
//        }
//        List<RecordVideo> recordVideos = videoService.retrieveRecord(pageable, userId);

        return ResponseEntity.ok().body("recordVideos");

    }

    @ApiOperation(value = "?????? ????????????", notes = "???????????? ?????? ????????? , ???????????? ?????????.")
    @PutMapping("/update")
    public ResponseEntity updateVideo(@ModelAttribute UpdateVideoDto updateVideoDto) throws IOException {

        log.info("dto {}",updateVideoDto);
        log.info("thumbnail {}",updateVideoDto.getThumbnail().isEmpty());
        log.info("tags {}",updateVideoDto.getTags().isEmpty());
        log.info("title {}",updateVideoDto.getTitle().isEmpty());

        if (!userLibrary.retrieveUserId().equals(updateVideoDto.getUsersId())) {
            throw new CustomException(ErrorCode.ACCESS_DENIED, "???????????? ?????? ????????? ????????????.");
        }

        if (updateVideoDto.getType().equals(VideoType.upload)) {
            log.info("uplaod type");
            videoService.updateUploadVideo(updateVideoDto);
            return ResponseEntity.ok().body("????????????");
        }

        videoService.updateEmbeddedVideo(updateVideoDto);
        log.info("emebedded ");
        return ResponseEntity.ok().body("????????????");
    }

    @DeleteMapping("/delete/{videoId}")
    public ResponseEntity deleteVideo(@PathVariable("videoId")Long videoId) {

        Long userId = userLibrary.retrieveUserId();
        if (userId == 0L)
            throw new CustomException(ErrorCode.NOT_OWNER,"????????? ????????? ????????????.");

        boolean bol = videoService.deleteVideo(videoId, userId);

        return ResponseEntity.ok().body("?????? ??????");
    }

    private void checkToken(HttpServletRequest request) {
        String guest = request.getHeader("guest");

        if(guest==null)
            throw new CustomException(ErrorCode.NOT_ENOUGH_HEADER);

        String token = resolveToken(request);
        log.info("guest :{}",guest);
        if(guest.equals("false")){
            jwtTokenProvider.validationTokenIn(token);
        }
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_TYPE)) {
            return  bearerToken.substring(7);
        }
        return null;
    }

    // ip ??????
     private String getIp() {
        HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String ip = req.getHeader("X-FORWARDED-FOR");
        if (ip == null)
            ip = req.getRemoteAddr();
        return ip;
    }

}
