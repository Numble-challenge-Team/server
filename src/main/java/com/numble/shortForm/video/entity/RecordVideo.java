package com.numble.shortForm.video.entity;

import com.numble.shortForm.time.BaseTime;
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


    @Column(name = "user_id")
    private Long userId;

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


    public RecordVideo(Long userId, Long videoId) {
        this.userId = userId;
        this.videoId = videoId;
    }
}
