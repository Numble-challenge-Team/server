package com.numble.shortForm.admin.response;

import com.numble.shortForm.user.entity.ProfileImg;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class UserAdminResponse {

    private String email;

    private String nickname;

    private ProfileImg profileImg;

    private LocalDate created_at;

    private Long reportCount;


    @QueryProjection
    public UserAdminResponse(String email, String nickname, ProfileImg profileImg, LocalDateTime created_at, Integer reportCount) {
        this.email = email;
        this.nickname = nickname;
        this.profileImg = profileImg;
        this.created_at = created_at.toLocalDate();
        this.reportCount = reportCount.longValue();
    }
}
