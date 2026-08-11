package com.ialmeida.marketpulse.portfolio.dto;

import java.time.Instant;

public class PositionResponse {

    private Long id;
    private String symbol;
    private String quantity;
    private String averageCost;
    private String currency;
    private Instant createdAt;
    private Instant updatedAt;

    public PositionResponse(Long id, String symbol, String quantity, String averageCost, String currency,
                           Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.symbol = symbol;
        this.quantity = quantity;
        this.averageCost = averageCost;
        this.currency = currency;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getAverageCost() {
        return averageCost;
    }

    public String getCurrency() {
        return currency;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
