package com.crudempleados.domain;

import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

@Component
public class PasswordPolicy {

    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final int MAX_PASSWORD_LENGTH = 64;
    private static final Pattern LETTER_PATTERN = Pattern.compile(".*[A-Za-z].*");
    private static final Pattern DIGIT_PATTERN = Pattern.compile(".*\\d.*");

    public void validateOrThrow(String rawPassword) {
        if (rawPassword == null
            || rawPassword.length() < MIN_PASSWORD_LENGTH
            || rawPassword.length() > MAX_PASSWORD_LENGTH
            || !LETTER_PATTERN.matcher(rawPassword).matches()
            || !DIGIT_PATTERN.matcher(rawPassword).matches()) {
            throw new IllegalArgumentException(
                "Field 'password' must satisfy rule: length 8-64 with at least one letter and one number.");
        }
    }
}