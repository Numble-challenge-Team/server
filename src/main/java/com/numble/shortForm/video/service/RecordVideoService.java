package com.numble.shortForm.video.service;

import com.numble.shortForm.video.entity.RecordVideo;
import com.numble.shortForm.video.repository.RecordVideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecordVideoService {

    private final RecordVideoRepository recordVideoRepository;


    public List<Long> getRecordVideoList(Long videoId, Long userId, Pageable pageable) {
        System.out.println("접속");
        recordVideoRepository.findAllByVideo_IdAndUser_Id(videoId, userId, pageable);

//        for (RecordVideo recordVideo : allByVideoIdAndUserId) {
//            System.out.println(recordVideo.getVideoId());
//        }
//        Set<Long> collect = recordVideoRepository.findAllByVideoIdAndUserId(videoId, userId,pageable)
//                .stream().map(obj -> obj.getVideoId()).collect(Collectors.toSet());
//
//        return new ArrayList<>(collect);
    return null;
    }
}
