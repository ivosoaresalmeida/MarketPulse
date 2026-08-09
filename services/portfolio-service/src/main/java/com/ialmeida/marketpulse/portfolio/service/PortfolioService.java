package com.ialmeida.marketpulse.portfolio.service;

import com.ialmeida.marketpulse.portfolio.dto.CreatePortfolioRequest;
import com.ialmeida.marketpulse.portfolio.dto.PortfolioResponse;
import com.ialmeida.marketpulse.portfolio.model.Portfolio;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class PortfolioService {

    private final List<Portfolio> portfolios = new ArrayList<>();

    public List<PortfolioResponse> getPortfolios() {
        return portfolios.stream().map(this::toResponse).toList();
    }

    public PortfolioResponse createPortfolio(CreatePortfolioRequest request) {
        Portfolio portfolio = new Portfolio(
            (long) (portfolios.size() + 1),
            request.getName(),
            request.getBaseCurrency(),
            Instant.now()
        );

        portfolios.add(portfolio);

        return toResponse(portfolio);
    }

    private PortfolioResponse toResponse(Portfolio portfolio) {
        return new PortfolioResponse(
            portfolio.getId(),
            portfolio.getName(),
            portfolio.getBaseCurrency(),
            portfolio.getCreatedAt()
        );
    }
}