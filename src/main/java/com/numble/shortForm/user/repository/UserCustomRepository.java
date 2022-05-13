package com.numble.shortForm.user.repository;


import com.numble.shortForm.admin.response.UserAdminResponse;
import com.numble.shortForm.request.PageDto;
import com.numble.shortForm.user.dto.response.UserProfileDto;
import org.springframework.data.domain.Page;

public interface UserCustomRepository {
    Page<UserAdminResponse> getUserList(PageDto pageDto);

    UserProfileDto getProfile(Long userId);
}
