package com.numble.shortForm.hashtag.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.List;

import static javax.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class HashTag {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name ="hashtag_id")
    private Long id;

    private String tagName;

    @OneToMany(mappedBy = "hashTag",cascade = CascadeType.ALL)
    private List<VideoHash> videoHashes = new ArrayList<>();

    public HashTag(String tagName) {
        this.tagName = tagName;
    }


}
