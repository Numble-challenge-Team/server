package com.numble.shortForm.user.repository;

import com.numble.shortForm.admin.response.QUserAdminResponse;
import com.numble.shortForm.admin.response.UserAdminResponse;
import com.numble.shortForm.request.PageDto;
import com.numble.shortForm.user.entity.QUsers;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

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
                        users.created_at
                )).from(users)
                .offset(pageDto.getPage() * pageDto.getSize())
                .limit(pageDto.getSize())
                .fetch();
        return new PageImpl<>(fetch, PageRequest.of(pageDto.getPage(), pageDto.getSize()),fetch.size());
    }
}
