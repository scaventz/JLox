package com.scaventz.lox.exception;

public class UnexpectedCharacterException extends RuntimeException {
    @Override
    public synchronized Throwable fillInStackTrace() {
        return super.fillInStackTrace();
    }

    public UnexpectedCharacterException(String message) {
        super(message, null);
    }
}
