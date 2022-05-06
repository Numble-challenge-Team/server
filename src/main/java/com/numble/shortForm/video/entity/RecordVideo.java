package com.numble.shortForm.video.entity;

import com.numble.shortForm.time.BaseTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class RecordVideo extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recordVideo_id")
    private Long id;

    private Long userId;

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
