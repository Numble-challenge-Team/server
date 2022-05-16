package com.numble.shortForm.user.repository;

import com.numble.shortForm.admin.response.QUserAdminResponse;
import com.numble.shortForm.admin.response.UserAdminResponse;
import com.numble.shortForm.request.PageDto;
import com.numble.shortForm.user.dto.response.QUserProfileDto;
import com.numble.shortForm.user.dto.response.UserProfileDto;
import com.numble.shortForm.user.entity.QUsers;
import com.numble.shortForm.user.sort.UserSort;
import com.numble.shortForm.video.dto.response.Result;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static com.numble.shortForm.user.entity.QUsers.users;

@RequiredArgsConstructor
public class UserCustomRepositoryImpl implements UserCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<UserAdminResponse> getUserList(PageDto pageDto){

        List<UserAdminResponse> fetch = queryFactory.select(new QUserAdminResponse(
                        users.email,
                        users.nickname,
                        users.profileImg,
                        users.created_at,
                        users.reports.size()
                )).from(users)
                .offset(pageDto.getPage() * pageDto.getSize())
                .limit(pageDto.getSize())
                .fetch();
        return new PageImpl<>(fetch, PageRequest.of(pageDto.getPage(), pageDto.getSize()),fetch.size());
    }

    @Override
    public UserProfileDto getProfile(Long userId) {
        return queryFactory.select(new QUserProfileDto(
                users.id,
                users.profileImg,
                users.nickname,
                users.email,
                        users.created_at
        )).from(users)
                .where(users.id.eq(userId))
                .fetchOne();
    }

    @Override
    public Result retrieveAllUser(Pageable pageable) {

        List<UserAdminResponse> fetch = queryFactory.select(new QUserAdminResponse(
                        users.email,
                        users.nickname,
                        users.profileImg,
                        users.created_at,
                        users.reports.size()
                )).from(users)
                .offset(pageable.getPageNumber() * pageable.getPageSize())
                .limit(pageable.getPageSize())
                .orderBy(UserSort.sort(pageable))
                .fetch();


        return null;
    }
}
