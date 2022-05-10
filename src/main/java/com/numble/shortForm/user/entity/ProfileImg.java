package com.numble.shortForm.user.entity;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProfileImg {

    private String name;
    private String url;

    public ProfileImg(String name, String url) {
        this.name = name;
        this.url = url;
    }
}
