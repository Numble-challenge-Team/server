package com.numble.shortForm.video.entity;

import com.numble.shortForm.hashtag.entity.HashTag;
import com.numble.shortForm.hashtag.entity.VideoHash;
import com.numble.shortForm.time.BaseTime;
import com.numble.shortForm.user.entity.Users;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.List;

import static javax.persistence.GenerationType.IDENTITY;
import static javax.persistence.GenerationType.SEQUENCE;
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

    private Long view=0L;

    @Column(nullable = false)
    private String duration;

    private Long showId;

    @Enumerated(EnumType.STRING)
    private VideoType videoType;

    private boolean isBlock=false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_id")
    private Users users;

    @OneToMany(mappedBy = "video",cascade = CascadeType.ALL)
    private List<VideoHash> videoHashes = new ArrayList<>();

    @OneToMany(mappedBy = "video",cascade = CascadeType.ALL)
    private List<VideoLike> videoLikes = new ArrayList<>();

    @Builder
    public Video(String title, UploadThumbNail uploadThumbNail, String videoUrl, String context, VideoType videoType, boolean isBlock, Users users,String duration) {
        this.title = title;
        this.uploadThumbNail = uploadThumbNail;
        this.videoUrl = videoUrl;
        this.context = context;
        this.videoType = videoType;
        this.isBlock = isBlock;
        this.users = users;
        this.duration =duration;
    }

    public void addVideoHash(List<VideoHash> tags) {
        this.videoHashes = tags;
    }
}
