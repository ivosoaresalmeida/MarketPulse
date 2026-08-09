package com.ialmeida.marketpulse.portfolio.dto;

import java.time.Instant;

public class PortfolioResponse {

    private final Long id;
    private final String name;
    private final String baseCurrency;
    private final Instant createdAt;

    public PortfolioResponse(
        Long id,
        String name,
        String baseCurrency,
        Instant createdAt) {

        this.id = id;
        this.name = name;
        this.baseCurrency = baseCurrency;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getBaseCurrency() {
        return baseCurrency;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}