package com.numble.shortForm.hashtag.service;

import com.numble.shortForm.hashtag.entity.HashTag;
import com.numble.shortForm.hashtag.repository.HashTagRepository;
import com.numble.shortForm.hashtag.repository.VideoHashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HashTagService {

    private final HashTagRepository hashTagRepository;
    private final VideoHashRepository videoHashRepository;

    public void getTagByConcern(List<Long> recordVideoList) {

//        videoHashRepository.findAllByVideoIdIn(recordVideoList).stream().map(obj -> obj.get);
    }


    public List<HashTag> createTag(List<String> tags) {
        List<HashTag> hashTagList = new ArrayList<>();

        for (String tag : tags) {

            HashTag hashTag = hashTagRepository.findByTagName(tag).orElse(null);

            if(hashTag==null){
                HashTag save = hashTagRepository.save(new HashTag(tag));
                hashTagList.add(save);
                continue;
            }
            hashTagList.add(hashTag);
        }
        return hashTagList;

    }
}
