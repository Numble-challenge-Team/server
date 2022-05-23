package com.numble.shortForm.video.service;

import com.numble.shortForm.comment.service.CommentService;
import com.numble.shortForm.exception.CustomException;
import com.numble.shortForm.exception.ErrorCode;
import com.numble.shortForm.hashtag.entity.HashTag;
import com.numble.shortForm.hashtag.entity.VideoHash;
import com.numble.shortForm.hashtag.repository.VideoHashRepository;
import com.numble.shortForm.hashtag.service.HashTagService;
import com.numble.shortForm.config.security.AuthenticationFacade;
import com.numble.shortForm.upload.S3Uploader;
import com.numble.shortForm.user.entity.Users;
import com.numble.shortForm.user.repository.UsersRepository;
import com.numble.shortForm.video.dto.request.EmbeddedVideoRequestDto;
import com.numble.shortForm.video.dto.request.NormalVideoRequestDto;
import com.numble.shortForm.video.dto.request.UpdateVideoDto;
import com.numble.shortForm.video.dto.response.Result;
import com.numble.shortForm.video.dto.response.VideoDetailResponseDto;
import com.numble.shortForm.video.dto.response.VideoResponseDto;
import com.numble.shortForm.video.entity.*;
import com.numble.shortForm.video.repository.RecordVideoRepository;
import com.numble.shortForm.video.repository.VideoLikeRepository;
import com.numble.shortForm.video.repository.VideoRepository;
import com.numble.shortForm.video.vimeo.Vimeo;
import com.numble.shortForm.video.vimeo.VimeoLogic;
import com.numble.shortForm.video.vimeo.VimeoResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class VideoService {

    private final VideoRepository videoRepository;
    private final VideoLikeRepository videoLikeRepository;
    private final S3Uploader s3Uploader;
    private final UsersRepository usersRepository;
    private final HashTagService hashTagService;
    private final VideoHashRepository videoHashRepository;
    private final RedisTemplate redisTemplate;
    private final RecordVideoRepository recordVideoRepository;
    private final RecordVideoService recordVideoService;
    private final AuthenticationFacade authenticationFacade;
    private final CommentService commentService;
    private final VimeoLogic vimeoLogic;

    private static final int PAGE_SIZE =5;

    @Value("${vimeo.token}")
    private String vimeoToken;

    //embedded 영상 업로드
    public void uploadEmbeddedVideo(EmbeddedVideoRequestDto embeddedVideoRequestDto, Long usersId) throws IOException {

        Users users = usersRepository.findById(usersId).orElseThrow(()->{
            throw new CustomException(ErrorCode.NOT_FOUND_USER,"유저가 조회되지 않습니다.");
        });

        Thumbnail thumbnail;

        if (embeddedVideoRequestDto.getThumbnail() != null) {
            String url = s3Uploader.uploadFile(embeddedVideoRequestDto.getThumbnail(),"thumbnail");
            thumbnail = new Thumbnail(url,embeddedVideoRequestDto.getThumbnail().getOriginalFilename());
        }else{
            thumbnail = new Thumbnail(null,null);
        }

        Video video = Video.builder()
                .videoUrl(embeddedVideoRequestDto.getVideoUrl())
                .thumbnail(thumbnail)
                .title(embeddedVideoRequestDto.getTitle())
                .description(embeddedVideoRequestDto.getDescription())
                .videoType(VideoType.embedded)
                .isBlock(false)
                .users(users)
                .duration(embeddedVideoRequestDto.getDuration())
                .build();
        Video createdVideo = videoRepository.save(video);


        if(!embeddedVideoRequestDto.getTags().isEmpty()){

            List<HashTag> tags = hashTagService.createTag(embeddedVideoRequestDto.getTags());

            List<VideoHash> videoHashes = tags.stream().map(t -> new VideoHash(createdVideo, t))
                    .collect(Collectors.toList());
            createdVideo.addVideoHash(videoHashes);
        }

        videoRepository.save(createdVideo);

    }

    public Page<VideoResponseDto> retrieveAll(Pageable pageable) {
        return videoRepository.retrieveAll(pageable);
    }

    //로그인하지않은 상세피이지
    public VideoDetailResponseDto retrieveDetailNotLogin(Long videoId, String ip) throws IOException {
        String IsExistRedis = (String) redisTemplate.opsForValue().get(videoId + "/" + ip);
        if (IsExistRedis == null) {
            videoRepository.updateView(videoId);
            redisTemplate.opsForValue().set(videoId+"/"+ip,"Anonymous User",5L,TimeUnit.MINUTES);
        }
        // 기본적으로 id는 1부터 생성되기때문에, 0에는 일치하는 값이 없다.
        VideoResponseDto videoResponseDto = videoRepository.retrieveDetail(videoId,0L);

        List<String> tags = videoHashRepository.findAllByVideoId(videoId).stream().map(h ->h.getHashTag().getTagName())
                .collect(Collectors.toList());

//        if(videoResponseDto.getTags()!=null&& !videoResponseDto.getTags().isEmpty()){
            videoResponseDto.setTags(tags);
//        }
        videoResponseDto.setLiked(false);

        if(videoResponseDto.getVideoType() ==VideoType.upload){
            getIframeUrl(videoResponseDto);
        }

        return VideoDetailResponseDto.builder()
                .videoDetail(videoResponseDto)
                .comments(commentService.getCommentList(videoId,0L))
                .concernVideoList(retrieveConcernVideosNotLogin(PageRequest.of(0,5),videoId,0L))
                .build();
    }
    // 비디오 상세조회(로그인)
    public VideoDetailResponseDto retrieveDetail(Long videoId,String ip,Long userId) throws IOException {


        // Redis로 10분동안 같은 ip접속시 조회수 제한
        String IsExistRedis = (String) redisTemplate.opsForValue().get(videoId + "/" + ip);
        if (IsExistRedis == null) {
            videoRepository.updateView(videoId);
            redisTemplate.opsForValue().set(videoId+"/"+ip,"true",10L,TimeUnit.MINUTES);
        }

        VideoResponseDto videoResponseDto = videoRepository.retrieveDetail(videoId,userId);

        if(videoResponseDto.getVideoType() ==VideoType.upload){
            getIframeUrl(videoResponseDto);
        }
        // tag처리
         List<String> tags = videoHashRepository.findAllByVideoId(videoId).stream().map(h ->h.getHashTag().getTagName())
                 .collect(Collectors.toList());
            videoResponseDto.setTags(tags);


         //좋아요 눌렀는지 확인
        if (searchVideoLike(userId, videoId) != null) {
            videoResponseDto.setLiked(true);
        }else{
            videoResponseDto.setLiked(false);
        }
         // 로그 저장
        recordVideoRepository.save(new RecordVideo(userId,videoId));

        return VideoDetailResponseDto.builder()
                .videoDetail(videoResponseDto)
                .comments(commentService.getCommentList(videoId,userId))
                .concernVideoList(retrieveConcernVideosNotLogin(PageRequest.of(0,5),videoId,userId))
                .build();
    }

    private void getIframeUrl(VideoResponseDto videoResponseDto) throws IOException {
        log.info("들어옴");
        Vimeo vimeo = new Vimeo(vimeoToken);
        VimeoResponse videoInfo = vimeo.getVideoInfo(videoResponseDto.getUrl());
        JSONObject json = videoInfo.getJson();
        JSONObject obj =(JSONObject) json.get("embed");

        videoResponseDto.setUrl( String.valueOf(obj.get("html")));
    }


    // 내비디오리스트 조회
    public Result retrieveMyVideo(String userEmail, Pageable pageable) {

        Users users = usersRepository.findByEmail(userEmail).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER, ""));

        Result result = videoRepository.retrieveMyVideo(userEmail, pageable);
        List<VideoResponseDto> contents = result.getContents();

        contents =checkLiked(users, contents);

        result.setContents(contents);
        return result;
    }

    private List<VideoResponseDto> checkLiked (Users users, List<VideoResponseDto> contents) {
        for (VideoResponseDto videoResponseDto : contents) {

            //여기서 get사용 주의하자!!!!
            Video video = videoRepository.findById(videoResponseDto.getVideoId()).get();

            if (videoLikeRepository.existsByVideoAndUsers(video, users)) {
                log.info("likes 존재함!");
                videoResponseDto.setLiked(true);
                continue;
            }
            videoResponseDto.setLiked(false);
        }
        return contents;
    }

    // 좋아요 요청
    public boolean requestLikeVideo(String userEmail,Long videoId) {
        Users users = usersRepository.findByEmail(userEmail).orElseThrow(()-> new CustomException(ErrorCode.NOT_FOUND_USER));
        Video video = videoRepository.findById(videoId).orElseThrow(()-> new CustomException(ErrorCode.NOT_FOUND_VIDEO,String.format("[%d] 비디오 아이디가 조회되지 않습니다.",videoId)));

        if (existsVideoLike(users, video)) {
            videoLikeRepository.save(new VideoLike(users,video));
            return true;
        }
        videoLikeRepository.deleteByUsersAndVideo(users,video);
        return false;
    }

    private boolean existsVideoLike(Users users, Video video) {
       return videoLikeRepository.findByUsersAndVideo(users,video).isEmpty();
    }

    //관심 동영상 조회

    public Page<VideoResponseDto> retrieveConcernVideos(Pageable pageable,Long userId,Long videoId) {

        Users users = usersRepository.findById(userId).orElseThrow(()-> new CustomException(ErrorCode.NOT_FOUND_USER));

        //사용자가 봤던 비디오 id 리스트 가져옴 5개
        List<Long> recordVideoList = recordVideoService.getRecordVideoList(videoId, users.getId(),PageRequest.of(0,PAGE_SIZE, Sort.by("created_at").descending()));

        return null;
    }

    // 로그인하지 않은 관심영상
    public Page<VideoResponseDto> retrieveConcernVideosNotLogin(Pageable pageable,Long videoId,Long userId) {

        // videoid 로 tag id 조회
        List<Long> tagids = videoHashRepository.findAllByVideoId(videoId).stream().map(obj -> obj.getHashTag().getId()).collect(Collectors.toList());
        // tag조회 한걸로 tag id를 가진 video id 조회
        List<Long> videoids = videoHashRepository.findAllByHashTagIdIn(tagids).stream().map(obj -> obj.getVideo().getId()).collect(Collectors.toList());
        // 자기 자신은 제외
        videoids.remove(videoId);


        return videoRepository.retrieveConcernVideo(videoids,videoId,pageable,userId);
    }

        // 동여상 검색
    public Result searchVideoQuery(String query,Pageable pageable,Long userId) {
        Result result = videoRepository.searchVideoQuery(query, pageable, userId);

        List<VideoResponseDto> contents = result.getContents();
        if (userId != 0L) {
            Users users = usersRepository.findById(userId).get();
            result.setContents(checkLiked(users, contents));
            return result;
        }
        for (VideoResponseDto dto : contents) {
            dto.setLiked(false);
        }
        result.setContents(contents);
        return result;
    }


    //로그인한 메인 동영상 리스트
    public Result retrieveMainVideoList(Pageable pageable,Long userId) {
        Result result = videoRepository.retrieveMainVideo(pageable, userId);


        Users users = usersRepository.findById(userId).orElseThrow(()->
                new CustomException(ErrorCode.NOT_FOUND_USER));

        result.setContents(checkLiked(users,result.getContents()));

        return result;

    }

    //로그인하지않은 메인 동여상 리스트
    public Result retrieveMainVideoListNotLogin(Pageable pageable) {

        return  videoRepository.retrieveMainVideoNotLogin(pageable);

    }

    private VideoLike searchVideoLike(Long usersId, Long videoId) {

        Users users = usersRepository.getById(usersId);
        Video video = videoRepository.getById(videoId);
        return  videoLikeRepository.findByUsersAndVideo(users,video).orElse(null);
    }

    public void uploadDirectVideo(String videoEndPoint, NormalVideoRequestDto normalVideoRequestDto, Long userId) throws IOException {

        Users users = usersRepository.findById(userId).get();

        Thumbnail thumbnail;

        if (normalVideoRequestDto.getThumbnail() != null) {
            String url = s3Uploader.uploadFile(normalVideoRequestDto.getThumbnail(),"thumbnail");
            thumbnail = new Thumbnail(url,normalVideoRequestDto.getThumbnail().getOriginalFilename());
        }else{
            thumbnail = new Thumbnail(null,null);
        }


        Video video = Video.builder()
                .users(users)
                .videoUrl(videoEndPoint)
                .videoType(VideoType.upload)
                .description(normalVideoRequestDto.getDescription())
                .isBlock(false)
                .duration(normalVideoRequestDto.getDuration())
                .title(normalVideoRequestDto.getTitle())
                .thumbnail(thumbnail)
                .build();

        Video createdVideo = videoRepository.save(video);


        if(!normalVideoRequestDto.getTags().isEmpty()){

        List<HashTag> tags = hashTagService.createTag(normalVideoRequestDto.getTags());

        List<VideoHash> videoHashes = tags.stream().map(t -> new VideoHash(createdVideo, t))
                .collect(Collectors.toList());

        createdVideo.addVideoHash(videoHashes);
        }
        videoRepository.save(createdVideo);
    }

    public Result retrieveLikesVideos(Pageable pageable,Long userId) {

        Result result = videoRepository.retrieveLikesVideos(pageable, userId);

        List<VideoResponseDto> contents = result.getContents();

        for (VideoResponseDto content : contents) {
            content.setLiked(true);
        }
        result.setContents(contents);

        return result;
    }

    public void updateEmbeddedVideo(UpdateVideoDto updateVideoDto) throws IOException {
        log.info("embedded");

        Video video = videoRepository.getById(updateVideoDto.getVideoId());

        Video updateVideo = reFreshVideo(video, updateVideoDto);

        videoRepository.save(updateVideo);
    }

    private Video reFreshVideo(Video video, UpdateVideoDto updateVideoDto) throws IOException {

        Thumbnail thumbnail =new Thumbnail(null,null);
        List<VideoHash> videoHashes =null;

        if (!updateVideoDto.getThumbnail().isEmpty()) {
            log.info("upload !!");
            String url = s3Uploader.uploadFile(updateVideoDto.getThumbnail(),"thumbnail");
            thumbnail = new Thumbnail(url,updateVideoDto.getThumbnail().getOriginalFilename());
        }
        if (!updateVideoDto.getTags().isEmpty()) {
            videoHashRepository.deleteAllByVideo(video);

            List<HashTag> tags = hashTagService.createTag(updateVideoDto.getTags());
                    videoHashes = tags.stream().map(t -> new VideoHash(video, t))
                    .collect(Collectors.toList());
        }

        if (updateVideoDto.getType().equals(VideoType.upload)) {
            log.info("type check ");
            VimeoResponse vimeoResponse = vimeoLogic.deleteVimeo(video.getVideoUrl());
            log.info("remove originalVideo {}",vimeoResponse.getStatusCode());

            String videoCode = vimeoLogic.uploadNormalVideo(updateVideoDto.getVideo());

            updateVideoDto.setVideoUrl(videoCode);
        }

        video.updateVideo(updateVideoDto,thumbnail,videoHashes);

        return video;
    }



    public void updateUploadVideo(UpdateVideoDto updateVideoDto) throws IOException {
        Video video = videoRepository.getById(updateVideoDto.getVideoId());
        Video updateVideo = reFreshVideo(video, updateVideoDto);
        videoRepository.save(updateVideo);
    }


    public boolean deleteVideo(Long videoId, Long userId) {

        Video video = videoRepository.findById(videoId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_VIDEO));

        if (video.getUsers().getId() != userId) {
            throw new CustomException(ErrorCode.NOT_OWNER,"주인이 아닙니다.");
        }

        videoRepository.delete(video);

        if (videoRepository.existsById(videoId)) {
            return false;
        }
        return true;
    }


    public boolean deleteVideoAdmin(Long videoId) {
        Video video = videoRepository.findById(videoId).orElseThrow(() ->
                new CustomException(ErrorCode.NOT_FOUND_VIDEO));
        videoRepository.delete(video);

        if(videoRepository.existsById(videoId))
            return false;
        return true;
    }
}
