package com.numble.shortForm.comment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RecommentCount {
    private Long commentId;
    private Long count;

}
