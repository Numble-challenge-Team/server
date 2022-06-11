package com.numble.shortForm.report.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.numble.shortForm.user.entity.Users;
import com.numble.shortForm.video.entity.Video;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Report {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "report_id")
    private Long id;

    private String detail;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_id")
    private Users users;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "video_id")
    private Video video;


    public Report(Users users, Video video,String detail) {
        this.users = users;
        this.video = video;
        this.detail = detail;
    }
}
