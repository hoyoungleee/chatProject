package com.example.demo.common.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
    private final CodeInterface code;

    public CustomException(CodeInterface v) {
        super(v.getMessage());
        this.code = v;
    }


    public CustomException(CodeInterface v, String message) {
        super(v.getMessage() + message);
        this.code = v;
    }
}
