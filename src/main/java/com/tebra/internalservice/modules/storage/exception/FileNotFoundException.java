package com.tebra.internalservice.modules.storage.exception;

public class FileNotFoundException extends RuntimeException {
    public FileNotFoundException(Long id) {
        super("File with id " + id + " not found");
    }
}
