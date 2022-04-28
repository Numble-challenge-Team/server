package com.numble.shortForm.video.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UploadThumbNail {

    private String uploadThumbName;
    private String storeThumbName;

    public UploadThumbNail(String uploadThumbName, String storeThumbName) {
        this.uploadThumbName = uploadThumbName;
        this.storeThumbName = storeThumbName;
    }
}
