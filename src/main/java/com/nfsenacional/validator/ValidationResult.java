package com.nfsenacional.validator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Resultado de uma validação — porte de {@code Nfse\Validator\ValidationResult} (php-api).
 *
 * @author Renato
 */
public final class ValidationResult {

    private final boolean valid;
    private final List<String> errors;

    private ValidationResult(boolean valid, List<String> errors) {
        this.valid = valid;
        this.errors = errors;
    }

    public static ValidationResult success() {
        return new ValidationResult(true, Collections.emptyList());
    }

    public static ValidationResult failure(List<String> errors) {
        return new ValidationResult(false, new ArrayList<>(errors));
    }

    public boolean isValid() {
        return valid;
    }

    public List<String> getErrors() {
        return errors;
    }
}
