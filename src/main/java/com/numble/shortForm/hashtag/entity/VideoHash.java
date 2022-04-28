package com.numble.shortForm.hashtag.entity;

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

}
