package com.ialmeida.marketpulse.portfolio.service;

import com.ialmeida.marketpulse.portfolio.client.UserClient;
import com.ialmeida.marketpulse.portfolio.dto.CreatePortfolioRequest;
import com.ialmeida.marketpulse.portfolio.dto.PortfolioResponse;
import com.ialmeida.marketpulse.portfolio.dto.UpdatePortfolioRequest;
import com.ialmeida.marketpulse.portfolio.exception.PortfolioNotFoundException;
import com.ialmeida.marketpulse.portfolio.exception.UserNotFoundException;
import com.ialmeida.marketpulse.portfolio.mapper.PortfolioMapper;
import com.ialmeida.marketpulse.portfolio.model.Portfolio;
import com.ialmeida.marketpulse.portfolio.repository.PortfolioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final PortfolioMapper portfolioMapper;
    private final UserClient userClient;

    public PortfolioService(
        PortfolioRepository portfolioRepository,
        PortfolioMapper portfolioMapper,
        UserClient userClient
    ) {
        this.portfolioRepository = portfolioRepository;
        this.portfolioMapper = portfolioMapper;
        this.userClient = userClient;
    }

    public PortfolioResponse createPortfolio(
        CreatePortfolioRequest request
    ) {
        if (!userClient.existsById(request.getUserId())) {
            throw new UserNotFoundException(request.getUserId());
        }

        Portfolio portfolio = new Portfolio(
            request.getUserId(),
            request.getName(),
            request.getBaseCurrency()
        );

        Portfolio savedPortfolio =
            portfolioRepository.save(portfolio);

        return portfolioMapper.toResponse(savedPortfolio);
    }

    public List<PortfolioResponse> getPortfolios() {

        return portfolioRepository.findAll()
            .stream()
            .map(portfolioMapper::toResponse)
            .toList();
    }

    public List<PortfolioResponse> getPortfoliosByUserId(Long userId) {

        return portfolioRepository.findByUserId(userId)
            .stream()
            .map(portfolioMapper::toResponse)
            .toList();
    }

    public PortfolioResponse getPortfolio(Long id) {

        Portfolio portfolio = portfolioRepository.findById(id)
            .orElseThrow(() ->
                new PortfolioNotFoundException(id)
            );

        return portfolioMapper.toResponse(portfolio);
    }

    public PortfolioResponse updatePortfolio(
        Long id,
        UpdatePortfolioRequest request
    ) {

        Portfolio portfolio = portfolioRepository.findById(id)
            .orElseThrow(() ->
                new PortfolioNotFoundException(id)
            );

        portfolio.update(
            request.getName(),
            request.getBaseCurrency()
        );

        Portfolio updatedPortfolio =
            portfolioRepository.save(portfolio);

        return portfolioMapper.toResponse(updatedPortfolio);
    }

    public void deletePortfolio(Long id) {

        Portfolio portfolio = portfolioRepository.findById(id)
            .orElseThrow(() ->
                new PortfolioNotFoundException(id)
            );

        portfolioRepository.delete(portfolio);
    }
}