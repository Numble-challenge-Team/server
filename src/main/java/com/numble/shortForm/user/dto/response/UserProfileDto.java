package com.numble.shortForm.user.dto.response;

import com.numble.shortForm.user.entity.ProfileImg;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserProfileDto {

    private Long usersId;

    private ProfileImg profileImg;

    private String nickname;

    private String email;


    @QueryProjection
    public UserProfileDto(Long usersId, ProfileImg profileImg, String nickname, String email) {
        this.usersId = usersId;
        this.profileImg = profileImg;
        this.nickname = nickname;
        this.email = email;
    }
}
