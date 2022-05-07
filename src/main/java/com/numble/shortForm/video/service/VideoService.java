package com.numble.shortForm.video.service;

import com.numble.shortForm.exception.CustomException;
import com.numble.shortForm.exception.ErrorCode;
import com.numble.shortForm.hashtag.entity.HashTag;
import com.numble.shortForm.hashtag.entity.VideoHash;
import com.numble.shortForm.hashtag.repository.VideoHashRepository;
import com.numble.shortForm.hashtag.service.HashTagService;
import com.numble.shortForm.security.AuthenticationFacade;
import com.numble.shortForm.upload.S3Uploader;
import com.numble.shortForm.user.entity.Users;
import com.numble.shortForm.user.repository.UsersRepository;
import com.numble.shortForm.video.dto.request.EmbeddedVideoRequestDto;
import com.numble.shortForm.video.dto.response.VideoResponseDto;
import com.numble.shortForm.video.entity.*;
import com.numble.shortForm.video.repository.RecordVideoRepository;
import com.numble.shortForm.video.repository.VideoLikeRepository;
import com.numble.shortForm.video.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    private static final int PAGE_SIZE =5;
    //embedded 영상 업로드
    public void uploadEmbeddedVideo(EmbeddedVideoRequestDto embeddedVideoRequestDto, Long usersId) throws IOException {

        Users users = usersRepository.findById(usersId).orElseThrow(()->{
            throw new CustomException(ErrorCode.NOT_FOUND_USER,"유저가 조회되지 않습니다.");
        });

        Thumbnail thumbnail;

        if (embeddedVideoRequestDto.getThumbNail() != null) {
            String url = s3Uploader.uploadFile(embeddedVideoRequestDto.getThumbNail(),"thumbnail");
            thumbnail = new Thumbnail(url,embeddedVideoRequestDto.getThumbNail().getOriginalFilename());
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

        if (embeddedVideoRequestDto.getTags().isEmpty()) {
            return;
        }

        List<HashTag> tags = hashTagService.createTag(embeddedVideoRequestDto.getTags());

        List<VideoHash> videoHashes = tags.stream().map(t -> new VideoHash(createdVideo, t))
                .collect(Collectors.toList());

        createdVideo.addVideoHash(videoHashes);
        videoRepository.save(createdVideo);

    }

    public Page<VideoResponseDto> retrieveAll(Pageable pageable) {
        return videoRepository.retrieveAll(pageable);
    }

    //로그인하지않은 상세피이지
    public VideoResponseDto retrieveDetailNotLogin(Long videoId, String ip) {
        String IsExistRedis = (String) redisTemplate.opsForValue().get(videoId + "/" + ip);
        if (IsExistRedis == null) {
            videoRepository.updateView(videoId);
            redisTemplate.opsForValue().set(videoId+"/"+ip,"Anonymous User",5L,TimeUnit.MINUTES);
        }

        VideoResponseDto videoResponseDto = videoRepository.retrieveDetail(videoId);
        List<String> tags = videoHashRepository.findAllByVideoId(videoId).stream().map(h ->h.getHashTag().getTagName())
                .collect(Collectors.toList());

        videoResponseDto.setTags(tags);
        videoResponseDto.setLiked(false);
        return videoResponseDto;
    }
    // 비디오 상세조회(로그인)
    public VideoResponseDto retrieveDetail(Long videoId,String ip,Long userId) {


        // Redis로 5분동안 같은 ip접속시 조회수 제한
        String IsExistRedis = (String) redisTemplate.opsForValue().get(videoId + "/" + ip);
        if (IsExistRedis == null) {
            videoRepository.updateView(videoId);
            redisTemplate.opsForValue().set(videoId+"/"+ip,"true",5L,TimeUnit.MINUTES);
        }


        VideoResponseDto videoResponseDto = videoRepository.retrieveDetail(videoId);

         List<String> tags = videoHashRepository.findAllByVideoId(videoId).stream().map(h ->h.getHashTag().getTagName())
                 .collect(Collectors.toList());

         videoResponseDto.setTags(tags);
         //좋아요 눌렀는지 확인
        if (searchVideoLike(userId, videoId) != null) {
            videoResponseDto.setLiked(true);
        }
        videoResponseDto.setLiked(false);
         // 로그 저장

        recordVideoRepository.save(new RecordVideo(videoId,userId));

        return videoResponseDto;
    }


    // 내비디오리스트 조회
    public Page<VideoResponseDto> retrieveMyVideo(String userEmail, Pageable pageable) {
        return videoRepository.retrieveMyVideo(userEmail,pageable);
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


//        for (Long aLong : recordVideoList) {
//            Video video = videoRepository.findById(aLong).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_VIDEO, "db에러 관리자에게 문의하세요"));
//            List<VideoHash> videoHashes = video.getVideoHashes();
//            for (VideoHash videoHash : videoHashes) {
//                System.out.println(videoHash.getHashTag().getTagName());
//            }
//        }
//
//        hashTagService.getTagByConcern(recordVideoList);
//
//        videoRepository.getVideoByTag(videoId);


        return null;
    }
    // 로그인하지 않은 관심영상
    public Page<VideoResponseDto> retrieveConcernVideosNotLogin(Pageable pageable,Long videoId) {

        // videoid 로 tag id 조회
        List<Long> tagids = videoHashRepository.findAllByVideoId(videoId).stream().map(obj -> obj.getHashTag().getId()).collect(Collectors.toList());
        // tag조회 한걸로 tag id를 가진 video id 조회
        List<Long> videoids = videoHashRepository.findAllByHashTagIdIn(tagids).stream().map(obj -> obj.getVideo().getId()).collect(Collectors.toList());
        // 자기 자신은 제외
        videoids.remove(videoId);


        return videoRepository.retrieveConcernVideo(videoids,pageable);
    }

        // 동여상 검색
    public Page<VideoResponseDto> searchVideoQuery(String query,Pageable pageable) {
        return videoRepository.searchVideoQuery(query,pageable);
    }

    //로그인한 메인 동영상 리스트
    public Page<VideoResponseDto> retrieveMainVideoList(Pageable pageable,Long userId) {
        Page<VideoResponseDto> videoResponseDtos = videoRepository.retrieveMainVideo(pageable);

        Users users = usersRepository.findById(userId).orElseThrow(()->
                new CustomException(ErrorCode.NOT_FOUND_USER));

        for (VideoResponseDto videoResponseDto : videoResponseDtos) {

            //여기서 get사용 주의하자!!!!
            Video video = videoRepository.findById(videoResponseDto.getVideoId()).get();

            if (videoLikeRepository.existsByVideoAndUsers(video, users)) {
                log.info("likes 존재함!");
                videoResponseDto.setLiked(true);
                continue;
            }
            videoResponseDto.setLiked(false);
        }


        return videoResponseDtos;

    }

    //로그인하지않은 메인 동여상 리스트
    public Page<VideoResponseDto> retrieveMainVideoListNotLogin(Pageable pageable) {

        return  videoRepository.retrieveMainVideoNotLogin(pageable);

    }

    private VideoLike searchVideoLike(Long usersId, Long videoId) {

        Users users = usersRepository.getById(usersId);
        Video video = videoRepository.getById(videoId);
        return  videoLikeRepository.findByUsersAndVideo(users,video).orElse(null);
    }
}
