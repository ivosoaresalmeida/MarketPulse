package com.ialmeida.marketpulse.portfolio.exception;

public class PositionNotFoundException extends RuntimeException {

    public PositionNotFoundException(Long id) {
        super("Position with id " + id + " was not found.");
    }
}