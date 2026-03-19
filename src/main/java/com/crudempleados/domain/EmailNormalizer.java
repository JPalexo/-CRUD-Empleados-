package com.crudempleados.domain;

import java.util.Locale;
import org.springframework.stereotype.Component;

@Component
public class EmailNormalizer {

    public String normalize(String rawEmail) {
        if (rawEmail == null) {
            return null;
        }
        return rawEmail.trim().toLowerCase(Locale.ROOT);
    }
}