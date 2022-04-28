package com.numble.shortForm.video.entity;

import com.numble.shortForm.time.BaseTime;
import com.numble.shortForm.user.entity.Users;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@NoArgsConstructor(access = PROTECTED)
@Getter
public class Video extends BaseTime {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "video_id")
    private Long id;

    private String title;

    @Embedded
    private UploadThumbNail uploadThumbNail;

    private String videoUrl;

    private String context;

    private Long view;

    @GeneratedValue(strategy = IDENTITY)
    private Long showId;

    @Enumerated(EnumType.STRING)
    private VideoType videoType;

    @ColumnDefault("false")
    private boolean isBlock;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_id")
    private Users users;

    @Builder
    public Video(String title, UploadThumbNail uploadThumbNail, String videoUrl, String context, VideoType videoType, boolean isBlock, Users users) {
        this.title = title;
        this.uploadThumbNail = uploadThumbNail;
        this.videoUrl = videoUrl;
        this.context = context;
        this.videoType = videoType;
        this.isBlock = isBlock;
        this.users = users;
    }
}
