package com.numble.shortForm.exception;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.ResponseEntity;

import java.time.OffsetDateTime;

@Getter
@Setter
@Builder
public class NotFoundUser {

    private final OffsetDateTime time = OffsetDateTime.now();

    @ApiModelProperty(example = "404")
    private final int status;
    @ApiModelProperty(example = "ACCESS_DENIED")
    private final String error;
    @ApiModelProperty(example = "NOT_FOUND_USER")
    private final String code;
    @ApiModelProperty(example = "다시한번 확인하세요.")
    private final String detail;
    @ApiModelProperty(example = "유저를 찾을수 없습니다.")
    private final String message;

    public static ResponseEntity<ErrorResponse> toResponseEntity(CustomException e) {
        ErrorCode errorCode = e.getErrorCode();

        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ErrorResponse.builder()
                        .status(errorCode.getHttpStatus().value())
                        .error(errorCode.getHttpStatus().name())
                        .code(errorCode.name())
                        .detail(errorCode.getDetail())
                        .message(e.getMessage())
                        .build());
    }
}
