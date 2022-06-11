package com.numble.shortForm.exception;

import lombok.Getter;
import org.springframework.web.bind.annotation.GetMapping;

@Getter
public class CustomException extends RuntimeException {

    private final ErrorCode errorCode;

    public CustomException(ErrorCode errorCode) {
        System.out.println("Custome Exceptoin run");
        this.errorCode = errorCode;
    }

    public CustomException(ErrorCode errorCode, String message) {
        super(message);
        System.out.println("Custome Exceptoin run");
        this.errorCode = errorCode;
    }
}
