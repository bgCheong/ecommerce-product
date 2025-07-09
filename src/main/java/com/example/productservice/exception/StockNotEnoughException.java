package com.example.productservice.exception;

public class StockNotEnoughException extends RuntimeException {
    public StockNotEnoughException(String message) {
        super(message);
    }
}