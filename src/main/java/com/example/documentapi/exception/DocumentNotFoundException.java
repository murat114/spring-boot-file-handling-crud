package com.example.documentapi.exception;

public class DocumentNotFoundException extends RuntimeException {

    public DocumentNotFoundException(Long id) {
        super("Dokument mit der ID " + id + " wurde nicht gefunden.");
    }
}