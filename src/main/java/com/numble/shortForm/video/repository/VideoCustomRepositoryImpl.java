package com.numble.shortForm.video.repository;

import com.numble.shortForm.request.PageDto;
import com.numble.shortForm.user.entity.QUsers;
import com.numble.shortForm.video.dto.response.QVideoResponseDto;
import com.numble.shortForm.video.dto.response.Result;
import com.numble.shortForm.video.dto.response.VideoResponseDto;
import com.numble.shortForm.video.entity.QVideoLike;
import com.numble.shortForm.video.entity.VideoLike;
import com.numble.shortForm.video.sort.VideoSort;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.swagger.models.auth.In;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static com.numble.shortForm.user.entity.QUsers.users;
import static com.numble.shortForm.video.entity.QVideo.video;
import static com.numble.shortForm.video.entity.QVideoLike.videoLike;

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
                        video.thumbnail,
                        video.isBlock,
                        video.view,
                        video.created_at,
                        video.duration,
                        video.videoLikes.size(),
                users.profileImg
                )).from(video)
                .leftJoin(video.users,users)
                .orderBy(video.showId.desc())
                .offset(pageable.getPageNumber()* pageable.getPageSize())
                .limit(pageable.getPageSize())
                .fetch();
        return new PageImpl<>(fetch,pageable,fetch.size());
    }

    @Override
    public VideoResponseDto retrieveDetail(Long videoId, Long userId) {

        return queryFactory.select(new QVideoResponseDto(
                video.id,
                users.id,
                users.nickname,
                video.showId,
                video.title,
                video.thumbnail,
                video.isBlock,
                video.view,
                video.created_at,
                video.duration,
                video.videoLikes.size(),
                video.description,
                users.id.eq(userId),
                video.videoType,
                video.videoUrl,
                users.profileImg
        )).from(video)
                .leftJoin(video.users,users)
                .where(video.id.eq(videoId))
                .fetchOne();

    }

    @Override
    public Result retrieveMyVideo(String userEmail, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();

        List<VideoResponseDto> fetch = queryFactory.select(new QVideoResponseDto(
                        video.id,
                        users.id,
                        users.nickname,
                        video.showId,
                        video.title,
                        video.thumbnail,
                        video.isBlock,
                        video.view,
                        video.created_at,
                        video.duration,
                        video.videoLikes.size(),
                        video.description,
                        users.isNotNull(),
                        video.videoType,
                users.profileImg
                )).from(video)
                .leftJoin(video.users,users)
//                .orderBy(video.created_at.desc())
                .where(users.email.eq(userEmail))
                .offset(pageable.getPageNumber()* pageable.getPageSize())
                .limit(pageable.getPageSize())
                .orderBy(VideoSort.sort(pageable))
                .fetch();

        int size = queryFactory.select(new QVideoResponseDto(
                        video.id,
                        users.id,
                        users.nickname,
                        video.showId,
                        video.title,
                        video.thumbnail,
                        video.isBlock,
                        video.view,
                        video.created_at,
                        video.duration,
                        video.videoLikes.size(),
                users.profileImg
                )).from(video)
                .leftJoin(video.users, users)
                .where(users.email.eq(userEmail))
                .offset((pageable.getPageNumber() + 1) * pageable.getPageSize())
                .limit(1)
                .fetch().size();

       return new Result(size >0 ?true : false,fetch,fetch.size());

    }

    //메인비디오 추천 반환
    @Override
    public List<VideoResponseDto> getVideoByTag(Long videoId) {

     return null;

    }

    @Override
    public Result searchVideoQuery(String query,Pageable pageable,Long userId) {

        List<VideoResponseDto> fetch = queryFactory.select(new QVideoResponseDto(
                        video.id,
                        users.id,
                        users.nickname,
                        video.showId,
                        video.title,
                        video.thumbnail,
                        video.isBlock,
                        video.view,
                        video.created_at,
                        video.duration,
                        video.videoLikes.size(),
                        video.description,
                        users.id.eq(userId),
                        video.videoType,
                users.profileImg
                )).from(video)
                .leftJoin(video.users,users)
                .where(video.title.contains(query).or(video.description.contains(query)))
                .orderBy(VideoSort.sort(pageable))
                .offset(pageable.getPageNumber()* pageable.getPageSize())
                .limit(pageable.getPageSize())
                .fetch();


        int size = queryFactory.select(new QVideoResponseDto(
                        video.id
                )).from(video)
                .leftJoin(video.users, users)
                .where(video.title.contains(query).or(video.description.contains(query)))
                .offset((pageable.getPageNumber() +1)* pageable.getPageSize())
                .limit(1)
                .fetch().size();

        int total = queryFactory.select(new QVideoResponseDto(
                        video.id
                )).from(video)
                .leftJoin(video.users, users)
                .where(video.title.contains(query).or(video.description.contains(query)))
                .fetch().size();

        return new Result(size >0 ?true : false,fetch,fetch.size(),total);
    }

    @Override
    public Result retrieveMainVideo(Pageable pageable,Long userId) {

        List<VideoResponseDto> fetch = queryFactory.select(new QVideoResponseDto(
                        video.id,
                        users.id,
                        users.nickname,
                        video.showId,
                        video.title,
                        video.thumbnail,
                        video.isBlock,
                        video.view,
                        video.created_at,
                        video.duration,
                        video.videoLikes.size(),
                        video.description,
                        users.id.eq(userId),
                        video.videoType,
                users.profileImg
                )).from(video)
                .leftJoin(video.users, users)
                .orderBy(VideoSort.sort(pageable))
                .offset(pageable.getPageNumber()* pageable.getPageSize())
                .limit(pageable.getPageSize())
                .fetch();

        int size = queryFactory.select(new QVideoResponseDto(
                        video.id
                )).from(video)
                .leftJoin(video.users, users)
                .offset((pageable.getPageNumber() + 1) * pageable.getPageSize())
                .limit(1)
                .fetch().size();

        return new Result(size >0 ?true : false,fetch,fetch.size());
    }

    @Override
    public Result retrieveMainVideoNoOffset(Long videoId,Long userId, Pageable pageable) {
        List<VideoResponseDto> fetch = queryFactory.select(new QVideoResponseDto(
                        video.id,
                        users.id,
                        users.nickname,
                        video.showId,
                        video.title,
                        video.thumbnail,
                        video.isBlock,
                        video.view,
                        video.created_at,
                        video.duration,
                        video.videoLikes.size(),
                        video.description,
                        users.id.eq(userId),
                        video.videoType,
                        users.profileImg
                )).from(video)
                .leftJoin(video.users, users)
                .orderBy(VideoSort.sort(pageable))
                .offset(pageable.getPageNumber()* pageable.getPageSize())
                .limit(pageable.getPageSize())
                .fetch();

        int size = queryFactory.select(new QVideoResponseDto(
                        video.id
                )).from(video)
                .leftJoin(video.users, users)
                .offset((pageable.getPageNumber() + 1) * pageable.getPageSize())
                .limit(1)
                .fetch().size();

        return new Result(size >0 ?true : false,fetch,fetch.size());
    }

    @Override
    public Result retrieveMainVideoNotLogin(Pageable pageable) {

        List<VideoResponseDto> fetch = queryFactory.select(new QVideoResponseDto(
                        video.id,
                        users.id,
                        users.nickname,
                        video.showId,
                        video.title,
                        video.thumbnail,
                        video.isBlock,
                        video.view,
                        video.created_at,
                        video.duration,
                        video.videoLikes.size(),
                        video.description,
                        video.isNull(),
                        video.videoType,
                users.profileImg
                )).from(video)
                .leftJoin(video.users, users)
                .orderBy(VideoSort.sort(pageable))
                .offset(pageable.getPageNumber()* pageable.getPageSize())
                .limit(pageable.getPageSize())
                .fetch();
        int size = queryFactory.select(new QVideoResponseDto(
                        video.id
                )).from(video)
                .leftJoin(video.users, users)
                .offset((pageable.getPageNumber() + 1) * pageable.getPageSize())
                .limit(1)
                .fetch().size();

        return new Result(size >0 ?true : false,fetch,fetch.size());
    }

    @Override
    public Result retrieveMainVideoNotLoginNoOffset(Long videoId, Pageable pageable) {

        List<VideoResponseDto> fetch = queryFactory.select(new QVideoResponseDto(
                        video.id,
                        users.id,
                        users.nickname,
                        video.showId,
                        video.title,
                        video.thumbnail,
                        video.isBlock,
                        video.view,
                        video.created_at,
                        video.duration,
                        video.videoLikes.size(),
                        video.description,
                        video.isNull(),
                        video.videoType,
                        users.profileImg
                )).from(video)
                .leftJoin(video.users, users)
                .where(ltVideoId(videoId))
                .orderBy(VideoSort.sort(pageable))
                .limit(pageable.getPageSize())
                .fetch();
        int size = queryFactory.select(new QVideoResponseDto(
                        video.id
                )).from(video)
                .leftJoin(video.users, users)
                .where(ltVideoId(videoId))
                .limit(1)
                .fetch().size();

        return new Result(size >0 ?true : false,fetch,fetch.size());
    }

    @Override
    public Page<VideoResponseDto> retrieveConcernVideo(List<Long> videoids,Long videoId, Pageable pageable,Long userId) {

        List<VideoResponseDto> fetch = queryFactory.select(new QVideoResponseDto(
                        video.id,
                        users.id,
                        users.nickname,
                        video.showId,
                        video.title,
                        video.thumbnail,
                        video.isBlock,
                        video.view,
                        video.created_at,
                        video.duration,
                        video.videoLikes.size(),
                        video.description,
                        users.id.eq(userId),
                    video.videoType,
                users.profileImg
                )).from(video)
                .leftJoin(video.users, users)
                .where(video.id.in(videoids).or(video.isNotNull()).and(video.id.ne(videoId)))
                .orderBy(video.videoLikes.size().desc())
                .offset(pageable.getPageNumber()* pageable.getPageSize())
                .limit(pageable.getPageSize())
                .fetch();
        return new PageImpl<>(fetch,pageable,fetch.size());
    }

    @Override
    public Result retrieveLikesVideos(Pageable pageable,Long userId) {
        List<VideoResponseDto> fetch = queryFactory.select(new QVideoResponseDto(
                        video.id,
                        users.id,
                        users.nickname,
                        video.showId,
                        video.title,
                        video.thumbnail,
                        video.isBlock,
                        video.view,
                        video.created_at,
                        video.duration,
                        video.videoLikes.size(),
                        video.description,
                        users.isNull(),
                        video.videoType,
                        users.profileImg
                )).from(video)
                .leftJoin(video.users,users)
                .where(video.id.in(
                        JPAExpressions
                                .select(videoLike.video.id)
                                .from(videoLike)
                                .where(videoLike.users.id.eq(userId))
                ))
                .offset(pageable.getPageNumber()* pageable.getPageSize())
                .limit(pageable.getPageSize())
                .orderBy(VideoSort.sort(pageable))
                .fetch();

        int size = queryFactory.select(new QVideoResponseDto(
                        video.id
                )).from(video)
                .leftJoin(video.users, users)
                .where(video.id.in(
                        JPAExpressions
                                .select(videoLike.video.id)
                                .from(videoLike)
                                .where(videoLike.users.id.eq(userId))
                ))
                .offset((pageable.getPageNumber() + 1) * pageable.getPageSize())
                .limit(1)
                .fetch().size();


        return new Result(size >0 ?true : false,fetch,fetch.size());

    }

    @Override
    public Result retrieveRecord(Pageable pageable, Long userId) {
        return null;
    }

    private BooleanExpression ltVideoId(Long videoId) {
        if (videoId == null) {
            return null;
        }
        return video.id.lt(videoId);
    }
}
