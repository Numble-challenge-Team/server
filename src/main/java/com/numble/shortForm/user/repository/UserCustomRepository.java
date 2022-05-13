package com.numble.shortForm.user.repository;


import com.numble.shortForm.admin.response.UserAdminResponse;
import com.numble.shortForm.request.PageDto;
import com.numble.shortForm.user.dto.response.UserProfileDto;
import com.numble.shortForm.video.dto.response.Result;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserCustomRepository {
    Page<UserAdminResponse> getUserList(PageDto pageDto);

    UserProfileDto getProfile(Long userId);

    Result retrieveAllUser(Pageable pageable);
}
