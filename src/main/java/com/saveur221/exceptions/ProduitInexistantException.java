package com.saveur221.exceptions;

import com.saveur221.exceptions.SaveurException;

public class ProduitInexistantException extends SaveurException {
    public ProduitInexistantException(String message) {
        super(message);

}
}