package com.km.peter.payment.exception;

public class FieldMissingException extends Exception {

    public FieldMissingException(String message) {
        super(message);
    }

    public FieldMissingException() {
    }
}
