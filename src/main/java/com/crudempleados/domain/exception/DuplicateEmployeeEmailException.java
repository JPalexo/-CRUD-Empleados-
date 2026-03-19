package com.crudempleados.domain.exception;

public class DuplicateEmployeeEmailException extends RuntimeException {

    public DuplicateEmployeeEmailException(String emailNormalizado) {
        super("Field 'email' already in use: " + emailNormalizado);
    }
}