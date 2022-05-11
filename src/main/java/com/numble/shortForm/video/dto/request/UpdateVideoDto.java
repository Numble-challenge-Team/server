package com.numble.shortForm.video.dto.request;

import com.numble.shortForm.video.entity.VideoType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.stylesheets.LinkStyle;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
public class UpdateVideoDto {

    @NotNull
    private Long usersId;

    @NotNull
    private Long videoId;

    @ApiModelProperty(value = "type",dataType="String", example="embedded / upload")
    private VideoType type;

    private String url=null;

    private MultipartFile thumbnail=null;

    private MultipartFile video=null;

    private List<String> tags=null;

    private Long duration=null;

    private String title=null;

    private String description=null;


}
