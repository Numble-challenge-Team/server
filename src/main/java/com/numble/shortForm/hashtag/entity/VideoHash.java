package com.numble.shortForm.hashtag.entity;

import com.numble.shortForm.user.entity.Users;
import com.numble.shortForm.video.entity.Video;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@NoArgsConstructor(access = PROTECTED)
@Getter
@ToString
public class VideoHash {

    @Id
    @Column(name="videoHash_id")
    @GeneratedValue(strategy = IDENTITY)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "video_id")
    private Video video;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hashtag_id")
    private HashTag hashTag;


    public VideoHash(Video video, HashTag hashTag) {
        this.video = video;
        this.hashTag = hashTag;
    }
}
