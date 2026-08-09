package com.ialmeida.marketpulse.portfolio.service;

import com.ialmeida.marketpulse.portfolio.dto.CreatePortfolioRequest;
import com.ialmeida.marketpulse.portfolio.dto.PortfolioResponse;
import com.ialmeida.marketpulse.portfolio.dto.UpdatePortfolioRequest;
import com.ialmeida.marketpulse.portfolio.exception.PortfolioNotFoundException;
import com.ialmeida.marketpulse.portfolio.model.Portfolio;
import com.ialmeida.marketpulse.portfolio.repository.PortfolioRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;

    public PortfolioService(PortfolioRepository portfolioRepository) {
        this.portfolioRepository = portfolioRepository;
    }

    public List<PortfolioResponse> getPortfolios() {
        return portfolioRepository.findAll()
            .stream()
            .map(this::toResponse)
            .toList();
    }

    public PortfolioResponse getPortfolio(Long id) {
        Portfolio portfolio = portfolioRepository.findById(id)
            .orElseThrow(() -> new PortfolioNotFoundException(id));

        return toResponse(portfolio);
    }

    public PortfolioResponse createPortfolio(CreatePortfolioRequest request) {

        Portfolio portfolio = new Portfolio(
            request.getName(),
            request.getBaseCurrency(),
            Instant.now()
        );

        Portfolio savedPortfolio = portfolioRepository.save(portfolio);

        return toResponse(savedPortfolio);
    }

    public PortfolioResponse updatePortfolio(
        Long id,
        UpdatePortfolioRequest request) {

        Portfolio portfolio = portfolioRepository.findById(id)
            .orElseThrow(() -> new PortfolioNotFoundException(id));

        portfolio.update(
            request.getName(),
            request.getBaseCurrency()
        );

        Portfolio updatedPortfolio = portfolioRepository.save(portfolio);

        return toResponse(updatedPortfolio);
    }

    public void deletePortfolio(Long id) {

        Portfolio portfolio = portfolioRepository.findById(id)
            .orElseThrow(() -> new PortfolioNotFoundException(id));

        portfolioRepository.delete(portfolio);
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