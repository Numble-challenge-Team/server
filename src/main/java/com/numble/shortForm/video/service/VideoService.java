package com.numble.shortForm.video.service;

import com.numble.shortForm.exception.CustomException;
import com.numble.shortForm.exception.ErrorCode;
import com.numble.shortForm.hashtag.entity.HashTag;
import com.numble.shortForm.hashtag.entity.VideoHash;
import com.numble.shortForm.hashtag.repository.HashTagRepository;
import com.numble.shortForm.hashtag.service.HashTagService;
import com.numble.shortForm.upload.S3Uploader;
import com.numble.shortForm.user.entity.Users;
import com.numble.shortForm.user.repository.UsersRepository;
import com.numble.shortForm.video.dto.request.EmbeddedVideoRequestDto;
import com.numble.shortForm.video.dto.response.VideoResponseDto;
import com.numble.shortForm.video.entity.UploadThumbNail;
import com.numble.shortForm.video.entity.Video;
import com.numble.shortForm.video.entity.VideoType;
import com.numble.shortForm.video.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional
public class VideoService {

    private final VideoRepository videoRepository;
    private final S3Uploader s3Uploader;
    private final UsersRepository usersRepository;
    private final HashTagService hashTagService;

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
}
