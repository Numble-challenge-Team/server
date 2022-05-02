package com.numble.shortForm.video.repository;

import com.numble.shortForm.request.PageDto;
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
                        video.created_at,
                        video.duration,
                        video.videoLikes.size()
                )).from(video)
                .leftJoin(video.users,users)
                .orderBy(video.showId.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        return new PageImpl<>(fetch,pageable,fetch.size());
    }

    @Override
    public VideoResponseDto retrieveDetail(Long videoId) {
        return queryFactory.select(new QVideoResponseDto(
                video.id,
                users.id,
                users.nickname,
                video.showId,
                video.title,
                video.uploadThumbNail,
                video.isBlock,
                video.view,
                video.created_at,
                video.duration,
                video.videoLikes.size()
        )).from(video)
                .leftJoin(video.users,users)
                .where(video.id.eq(videoId))
                .fetchOne();

    }

    @Override
    public Page<VideoResponseDto> retrieveMyVideo(String userEmail, PageDto pageDto) {
        List<VideoResponseDto> fetch = queryFactory.select(new QVideoResponseDto(
                        video.id,
                        users.id,
                        users.nickname,
                        video.showId,
                        video.title,
                        video.uploadThumbNail,
                        video.isBlock,
                        video.view,
                        video.created_at,
                        video.duration,
                        video.videoLikes.size()
                )).from(video)
                .leftJoin(video.users,users)
                .orderBy(video.created_at.desc())
                .where(users.email.eq(userEmail))
                .offset(pageDto.getSize()* pageDto.getPage())
                .limit(pageDto.getSize())
                .fetch();
        return new PageImpl<>(fetch,Pageable.ofSize(pageDto.getPage()),fetch.size());

    }
}
