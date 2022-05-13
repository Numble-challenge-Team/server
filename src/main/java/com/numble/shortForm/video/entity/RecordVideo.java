package com.numble.shortForm.video.entity;

import com.numble.shortForm.time.BaseTime;
import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

import static javax.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@NoArgsConstructor(access = PROTECTED)
@Getter
public class RecordVideo extends BaseTime {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "record_id")
    private Long id;


    @Column(name = "users_id")
    private Long usersId;

    @Column(name = "video_id")
    private Long videoId;


    @Override
    public LocalDateTime getCreated_at() {
        return super.getCreated_at();
    }

    @Override
    public LocalDateTime getUpdated_at() {
        return super.getUpdated_at();
    }


    @QueryProjection
    public RecordVideo(Long usersId, Long videoId) {
        this.usersId = usersId;
        this.videoId = videoId;
    }


}
