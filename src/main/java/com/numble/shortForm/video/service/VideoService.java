package com.numble.shortForm.video.service;

import com.numble.shortForm.exception.CustomException;
import com.numble.shortForm.exception.ErrorCode;
import com.numble.shortForm.hashtag.entity.HashTag;
import com.numble.shortForm.hashtag.entity.VideoHash;
import com.numble.shortForm.hashtag.repository.HashTagRepository;
import com.numble.shortForm.hashtag.repository.VideoHashRepository;
import com.numble.shortForm.hashtag.service.HashTagService;
import com.numble.shortForm.request.PageDto;
import com.numble.shortForm.upload.S3Uploader;
import com.numble.shortForm.user.entity.Users;
import com.numble.shortForm.user.repository.UsersRepository;
import com.numble.shortForm.video.dto.request.EmbeddedVideoRequestDto;
import com.numble.shortForm.video.dto.response.VideoResponseDto;
import com.numble.shortForm.video.entity.UploadThumbNail;
import com.numble.shortForm.video.entity.Video;
import com.numble.shortForm.video.entity.VideoLike;
import com.numble.shortForm.video.entity.VideoType;
import com.numble.shortForm.video.repository.VideoLikeRepository;
import com.numble.shortForm.video.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    public void uploadEmbeddedVideo(EmbeddedVideoRequestDto embeddedVideoRequestDto, Long usersId) throws IOException {

        Users users = usersRepository.findById(usersId).orElseThrow(()->{
            throw new CustomException(ErrorCode.NOT_FOUND_USER,"유저가 조회되지 않습니다.");
        });

        UploadThumbNail uploadThumbNail;

        if (embeddedVideoRequestDto.getThumbNail() != null) {
            String url = s3Uploader.uploadFile(embeddedVideoRequestDto.getThumbNail(),"thumbnail");
            uploadThumbNail = new UploadThumbNail(embeddedVideoRequestDto.getThumbNail().getOriginalFilename(),url);
        }else{
            uploadThumbNail = new UploadThumbNail(null,null);
        }

        Video video = Video.builder()
                .videoUrl(embeddedVideoRequestDto.getVideoUrl())
                .uploadThumbNail(uploadThumbNail)
                .title(embeddedVideoRequestDto.getTitle())
                .context(embeddedVideoRequestDto.getContext())
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

    // 비디오 상세조회
    public VideoResponseDto retrieveDetail(Long videoId,String ip,String userEmail) {

        String IsExistRedis = (String) redisTemplate.opsForValue().get(videoId + "/" + ip);
        if (IsExistRedis == null) {
            videoRepository.updateView(videoId);
            redisTemplate.opsForValue().set(videoId+"/"+ip,userEmail,5L,TimeUnit.MINUTES);
        }


        VideoResponseDto videoResponseDto = videoRepository.retrieveDetail(videoId);

         List<String> tags = videoHashRepository.findAllByVideoId(videoId).stream().map(h ->h.getHashTag().getTagName())
                 .collect(Collectors.toList());
         videoResponseDto.setTags(tags);

        return videoResponseDto;
    }

    public Page<VideoResponseDto> retrieveMyVideo(String userEmail, PageDto pageDto) {
        return videoRepository.retrieveMyVideo(userEmail,pageDto);
    }

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
}
