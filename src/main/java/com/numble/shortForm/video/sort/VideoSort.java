package com.numble.shortForm.video.sort;

import com.numble.shortForm.video.entity.QVideo;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import static com.numble.shortForm.video.entity.QVideo.video;

@Slf4j
public class VideoSort {


    public static OrderSpecifier<?> sort(Pageable pageable) {
        if (!pageable.getSort().isEmpty()) {
            log.info("orderSpecifier sort 진입");
            for (Sort.Order order : pageable.getSort()) {

                Order direction = order.getDirection().isAscending() ? Order.ASC : Order.DESC;

                switch (order.getProperty()) {
                    case "created_at":
                        return new OrderSpecifier<>(direction, video.created_at);
                    case "likes":
                        return new OrderSpecifier<>(direction,video.videoLikes.size());


                }
            }
        }
        return new OrderSpecifier<>(Order.DESC,video.videoLikes.size());
    }
}
