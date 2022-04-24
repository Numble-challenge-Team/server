package com.numble.shortForm.video.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@NoArgsConstructor(access = PROTECTED)
@Getter
public class Video {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "video_id")
    Long id;

}
