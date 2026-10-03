/*
 * Astro Invasion - class UsernameValidationResult -
 * Describes the outcome of centralized username validation.
 *
 * Developed & Designed by BIGA ©2024-2026. All rights reserved.
 */
package sorgente.Authentication;

public final class UsernameValidationResult {
    public enum Status {
        VALID,
        EMPTY,
        TOO_SHORT,
        TOO_LONG,
        INVALID_CHARACTERS,
        RESERVED,
        OFFENSIVE
    }

    private final Status status;

    private UsernameValidationResult(Status status) {
        this.status = status;
    }

    public static UsernameValidationResult of(Status status) {
        return new UsernameValidationResult(status);
    }

    public Status getStatus() {
        return status;
    }

    public boolean isValid() {
        return status == Status.VALID;
    }
}
