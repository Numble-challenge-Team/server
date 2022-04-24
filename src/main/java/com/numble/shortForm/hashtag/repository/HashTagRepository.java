package com.numble.shortForm.hashtag.repository;


import com.numble.shortForm.hashtag.entity.HashTag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HashTagRepository extends JpaRepository<HashTag,Long> {
}
