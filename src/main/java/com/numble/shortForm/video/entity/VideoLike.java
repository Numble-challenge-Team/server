package com.numble.shortForm.video.entity;

import com.numble.shortForm.user.entity.Users;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@NoArgsConstructor(access = PROTECTED)
@Getter
public class VideoLike {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "videoLike_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_id")
    private Users users;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "video_id")
    private Video video;

    public VideoLike(Users users, Video video) {
        this.users = users;
        this.video = video;
    }
}
