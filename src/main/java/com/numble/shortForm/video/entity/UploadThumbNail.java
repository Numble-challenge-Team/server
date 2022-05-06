package com.numble.shortForm.video.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UploadThumbNail {

    private String uploadThumbUrl;
    private String storeThumbName;

    public UploadThumbNail(String uploadThumbUrl, String storeThumbName) {
        this.uploadThumbUrl = uploadThumbUrl;
        this.storeThumbName = storeThumbName;
    }
}
