package com.ialmeida.marketpulse.portfolio.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;

public class UpdatePositionRequest {

    @NotBlank(message = "Symbol is required.")
    private String symbol;

    @NotNull(message = "Quantity is required.")
    @DecimalMin(
            value = "0.00000001",
            message = "Quantity must be greater than zero."
    )
    private BigDecimal quantity;

    @NotNull(message = "Average cost is required.")
    @DecimalMin(
            value = "0.00000001",
            message = "Average cost must be greater than zero."
    )
    private BigDecimal averageCost;

    @NotBlank(message = "Currency is required.")
    @Pattern(
            regexp = "^[A-Z]{3}$",
            message = "Currency must be a 3-letter uppercase currency code."
    )
    private String currency;

    public String getSymbol() {
        return symbol;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public BigDecimal getAverageCost() {
        return averageCost;
    }

    public String getCurrency() {
        return currency;
    }
}