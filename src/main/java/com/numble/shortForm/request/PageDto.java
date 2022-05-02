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


    @ApiModelProperty(value = "page number,0 부터 시작함!  ",example = "2")
    private int page;

}
