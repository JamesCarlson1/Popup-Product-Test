package com.inventory.service;

/**
 * Signals a domain-level problem (missing inventory, duplicate name, bad input,
 * underlying storage failure) that callers should show to the user rather than crash on.
 */
public class InventoryException extends RuntimeException {

    public InventoryException(String message) {
        super(message);
    }

    public InventoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
