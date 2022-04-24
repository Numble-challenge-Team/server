package com.numble.shortForm.exception;


import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.ResponseEntity;

import java.time.OffsetDateTime;

@Getter
@Setter
@Builder
@RequiredArgsConstructor
public class ErrorResponse {

    private final OffsetDateTime time = OffsetDateTime.now();
    @ApiModelProperty(example = "403")
    private final int status;
    @ApiModelProperty(example = "FORBIDDEN_ACCESS")
    private final String error;
    @ApiModelProperty(example = "BAD_REQUEST_PARAM")
    private final String code;
    @ApiModelProperty(example = "권한이 없습니다.")
    private final String detail;
    @ApiModelProperty(example = "토큰이 유효하지 않습니다.")
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
