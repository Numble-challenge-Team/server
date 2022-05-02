package com.numble.shortForm.exception;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.http.ResponseEntity;

import java.time.OffsetDateTime;

@Getter
@ToString
@Builder
public class ServerErrorResponse {
    private final OffsetDateTime time = OffsetDateTime.now();

    @ApiModelProperty(example = "500")
    private final int status;
    @ApiModelProperty(example = "ACCESS_DENIED")
    private final String error;
    @ApiModelProperty(example = "SERVER_ERROR")
    private final String code;
    @ApiModelProperty(example = "관리자에게 문의하세요.")
    private final String detail;
    @ApiModelProperty(example = "서버 에러입니다.")
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
