package com.saveur221.exceptions;
//classe mere toutes les exceptions metier de l'application
//il permet traitement centralise (catch SaveurException) au niveau des vues.

public class SaveurException extends RuntimeException {
    public SaveurException(String message) {
        super(message);
    }

    public SaveurException(String message, Throwable cause) {
        super(message, cause);
    }

}
