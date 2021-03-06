package com.numble.shortForm.video.dto.request;

import com.numble.shortForm.video.entity.VideoType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@ToString
public class UpdateVideoDto {

    private Long usersId;

    private Long videoId;

    @ApiModelProperty(value = "type",dataType="String", example="embedded / upload")
    private VideoType type;

    private String videoUrl =null;

    private MultipartFile thumbnail;

    private MultipartFile video;

    private List<String> tags=null;

    private Long duration=null;

    private String title=null;

    private String description=null;


}
