package com.ialmeida.marketpulse.portfolio.dto;

import java.time.Instant;
import java.util.List;

public class PortfolioResponse {

    private Long id;
    private Long userId;
    private String name;
    private String baseCurrency;
    private List<PositionResponse> positions;
    private Instant createdAt;
    private Instant updatedAt;

    public PortfolioResponse(
        Long id,
        Long userId,
        String name,
        String baseCurrency,
        List<PositionResponse> positions,
        Instant createdAt,
        Instant updatedAt
    ) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.baseCurrency = baseCurrency;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.positions = positions;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getBaseCurrency() {
        return baseCurrency;
    }

    public List<PositionResponse> getPositions() {
        return positions;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}