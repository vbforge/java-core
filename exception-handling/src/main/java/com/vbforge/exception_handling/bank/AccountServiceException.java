package com.vbforge.exception_handling.bank;

/**
 * UNCHECKED "wrapper" exception for a higher layer. Its two-argument constructor
 * keeps the original failure as the {@code cause}, so no diagnostic information is lost.
 */
public class AccountServiceException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public AccountServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
