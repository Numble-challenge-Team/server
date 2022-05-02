package com.numble.shortForm.response;

import com.numble.shortForm.user.dto.response.UserResponseDto;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;


@Getter
@Setter
@Builder
public class Response {

        @ApiModelProperty(example = "200")
        private String state;

        @ApiModelProperty(example = "success")
        private String result;

        @ApiModelProperty(example = "성공")
        private String message;

        @ApiModelProperty(example = "{accessToke =adsdfsvewr, refreshtoken=sfsdfsdffsdf}")
        private Object data;


    public static ResponseEntity<?> success(Object data, String msg, HttpStatus status) {
        Response response = Response.builder()
                .state(String.valueOf(status.value()))
                .data(data)
                .result("success")
                .message(msg)
                .build();
        return ResponseEntity.ok(response);
    }
    public static ResponseEntity<?> success(String msg) {
        return success(Collections.emptyList(), msg, HttpStatus.OK);
    }

}
