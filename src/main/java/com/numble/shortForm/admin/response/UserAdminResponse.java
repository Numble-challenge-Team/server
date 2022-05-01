package com.numble.shortForm.admin.response;

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

    private LocalDate created_at;

    @QueryProjection
    public UserAdminResponse(String email, String nickname, LocalDateTime created_at) {
        this.email = email;
        this.nickname = nickname;
        this.created_at = created_at.toLocalDate();
    }
}
