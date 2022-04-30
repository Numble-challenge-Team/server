package com.numble.shortForm.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PageDto {

    @ApiModelProperty(value = "가져올 개수",example = "3")
    private int size;

    @ApiModelProperty(value = "offset",example = "5")
    private int offset;

    @ApiModelProperty(value = "page number  ",example = "2")
    private int page;

}
