package com.numble.shortForm.user.dto.response;

import com.numble.shortForm.user.entity.ProfileImg;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class UserProfileDto {

    private Long usersId;

    private ProfileImg profileImg;

    private String nickname;

    private String email;

    private LocalDate created_at;


    @QueryProjection
    public UserProfileDto(Long usersId, ProfileImg profileImg, String nickname, String email , LocalDateTime created_at) {
        this.usersId = usersId;
        this.profileImg = profileImg;
        this.nickname = nickname;
        this.email = email;
        this.created_at = created_at.toLocalDate();
    }
}
