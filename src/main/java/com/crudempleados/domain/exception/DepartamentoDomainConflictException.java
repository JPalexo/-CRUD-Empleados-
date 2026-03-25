package com.crudempleados.domain.exception;

public class DepartamentoDomainConflictException extends RuntimeException {

    public DepartamentoDomainConflictException(String message) {
        super(message);
    }
}