package com.numble.shortForm.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
public class ResponseDto {

    @ApiModelProperty(value = "Http code",example = "200")
    private int state;

    @ApiModelProperty(value = "Result 코드",example = "success")
    private String result;

    @ApiModelProperty(value = "메시지",example = "응답 성공")
    private String message;

    @ApiModelProperty(value = "데이터")
    private Object data;


}
