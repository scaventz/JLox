package com.scaventz.lox;

public class Return extends RuntimeException {
    final Object value;

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this; // don't care about stack trace
    }

    Return(Object value) {
        super(null, null, false, false);
        this.value = value;
    }
}
