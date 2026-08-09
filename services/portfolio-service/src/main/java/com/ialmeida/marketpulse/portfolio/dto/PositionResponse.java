package com.ialmeida.marketpulse.portfolio.dto;

public class PositionResponse {

    private Long id;
    private String symbol;
    private String quantity;
    private String averageCost;
    private String currency;

    public PositionResponse(Long id, String symbol, String quantity, String averageCost, String currency) {
        this.id = id;
        this.symbol = symbol;
        this.quantity = quantity;
        this.averageCost = averageCost;
        this.currency = currency;
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
    
}
