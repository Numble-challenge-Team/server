package com.numble.shortForm.video.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VideoCode {

    private String code;
    private String url;

    public VideoCode(String code, String url) {
        this.code = code;
        this.url = url;
    }
}
