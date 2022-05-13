package com.numble.shortForm.admin.response;

import com.numble.shortForm.video.dto.response.VideoResponseDto;

import java.util.List;

public class ResultUsers {

    private boolean hasMore;

    private List<UserAdminResponse> contents;

    private int count;


    public ResultUsers(boolean hasMore, List<UserAdminResponse> contents, int count) {
        this.hasMore = hasMore;
        this.contents = contents;
        this.count = count;
    }
}
