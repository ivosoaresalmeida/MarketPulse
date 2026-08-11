package com.ialmeida.marketpulse.portfolio.mapper;

import com.ialmeida.marketpulse.portfolio.dto.PortfolioResponse;
import com.ialmeida.marketpulse.portfolio.dto.PositionResponse;
import com.ialmeida.marketpulse.portfolio.model.Portfolio;
import com.ialmeida.marketpulse.portfolio.model.Position;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PortfolioMapper {

    public PortfolioResponse toResponse(Portfolio portfolio) {

        List<PositionResponse> positions =
            portfolio.getPositions()
                .stream()
                .map(this::toPositionResponse)
                .toList();

        return new PortfolioResponse(
            portfolio.getId(),
            portfolio.getUserId(),
            portfolio.getName(),
            portfolio.getBaseCurrency(),
            positions,
            portfolio.getCreatedAt(),
            portfolio.getUpdatedAt()
        );
    }

    public PositionResponse toPositionResponse(Position position) {

        return new PositionResponse(
            position.getId(),
            position.getSymbol(),
            position.getQuantity().toString(),
            position.getAverageCost().toString(),
            position.getCurrency(),
            position.getCreatedAt(),
            position.getUpdatedAt()
        );
    }
}