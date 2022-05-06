package com.numble.shortForm.video.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Thumbnail {

    private String url;
    private String name;

    public Thumbnail(String url, String name) {
        this.url = url;
        this.name = name;
    }
}
