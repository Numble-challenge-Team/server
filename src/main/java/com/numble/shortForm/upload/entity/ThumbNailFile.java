package com.numble.shortForm.upload.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ThumbNailFile {

    private String uploadThumbName;
    private String storeThumbName;

    public ThumbNailFile(String uploadThumbName, String storeThumbName) {
        this.uploadThumbName = uploadThumbName;
        this.storeThumbName = storeThumbName;
    }
}
