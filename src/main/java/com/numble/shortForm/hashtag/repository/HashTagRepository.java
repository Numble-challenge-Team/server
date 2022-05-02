package com.numble.shortForm.hashtag.repository;


import com.numble.shortForm.hashtag.entity.HashTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HashTagRepository extends JpaRepository<HashTag,Long> {
    Optional<HashTag> findByTagName(String tag);


}
