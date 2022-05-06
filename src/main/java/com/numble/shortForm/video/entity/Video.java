package com.numble.shortForm.video.entity;

import com.numble.shortForm.hashtag.entity.VideoHash;
import com.numble.shortForm.time.BaseTime;
import com.numble.shortForm.user.entity.Users;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.List;

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
    private Thumbnail thumbnail;

    private String videoUrl;

    private String description;

    private Long view=0L;

    @Column(nullable = false)
    private Long duration;

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
    public Video(String title, Thumbnail thumbnail, String videoUrl, String description, VideoType videoType, boolean isBlock, Users users, Long duration) {
        this.title = title;
        this.thumbnail = thumbnail;
        this.videoUrl = videoUrl;
        this.description = description;
        this.videoType = videoType;
        this.isBlock = isBlock;
        this.users = users;
        this.duration =duration;
    }

    public void addVideoHash(List<VideoHash> tags) {
        this.videoHashes = tags;
    }
}
