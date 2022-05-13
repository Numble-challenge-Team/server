package com.numble.shortForm.user.sort;

import com.numble.shortForm.user.entity.QUsers;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import static com.numble.shortForm.user.entity.QUsers.users;


public class UserSort {

    public static OrderSpecifier<?> sort(Pageable pageable) {
        if (!pageable.getSort().isEmpty()) {
            for (Sort.Order order : pageable.getSort()) {

                Order direction = order.getDirection().isAscending() ? Order.ASC : Order.DESC;

                switch (order.getProperty()) {
                    case "created_at":
                        return new OrderSpecifier<>(direction, users.created_at);
                    case "reports":
                        return new OrderSpecifier<>(direction,users.reports.size());
                    case "id":
                        return new OrderSpecifier<>(direction,users.id);

                }
            }
        }
        return new OrderSpecifier<>(Order.DESC,users.created_at);
    }
}
