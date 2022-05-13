package com.numble.shortForm.video.dto.response;

import com.numble.shortForm.comment.dto.response.OriginalComment;
import lombok.*;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@Builder
public class VideoDetailResponseDto {

    public VideoResponseDto videoDetail;

    public List<OriginalComment> comments;

    public Page<VideoResponseDto> concernVideoList;


}
