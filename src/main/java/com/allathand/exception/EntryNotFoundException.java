package com.allathand.exception;

public class EntryNotFoundException extends RuntimeException {

    public EntryNotFoundException(String id) {
        super("Entry not found: " + id);
    }
}
