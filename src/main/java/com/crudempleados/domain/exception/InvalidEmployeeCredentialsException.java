package com.crudempleados.domain.exception;

public class InvalidEmployeeCredentialsException extends RuntimeException {

    public InvalidEmployeeCredentialsException() {
        super("Employee authentication failed.");
    }
}