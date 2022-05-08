package com.numble.shortForm.video.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Result {

    private boolean hasMore;

    private List<VideoResponseDto> contents;

    private int count;


    public Result(boolean hasMore, List<VideoResponseDto> contents, int count) {
        this.hasMore = hasMore;
        this.contents = contents;
        this.count = count;
    }
}
