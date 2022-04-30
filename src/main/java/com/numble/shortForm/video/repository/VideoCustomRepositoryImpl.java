package com.numble.shortForm.video.repository;

import com.numble.shortForm.user.entity.QUsers;
import com.numble.shortForm.video.dto.response.QVideoResponseDto;
import com.numble.shortForm.video.dto.response.VideoResponseDto;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static com.numble.shortForm.user.entity.QUsers.users;
import static com.numble.shortForm.video.entity.QVideo.video;

@RequiredArgsConstructor
public class VideoCustomRepositoryImpl implements VideoCustomRepository{

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<VideoResponseDto> retrieveAll(Pageable pageable) {
        List<VideoResponseDto> fetch = queryFactory.select(new QVideoResponseDto(
                        video.id,
                        users.id,
                        users.nickname,
                        video.showId,
                        video.title,
                        video.uploadThumbNail,
                        video.isBlock,
                        video.view,
                        video.created_at
                )).from(video)
                .leftJoin(video.users,users)
                .orderBy(video.showId.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        return new PageImpl<>(fetch,pageable,fetch.size());
    }


}
