package com.ialmeida.marketpulse.portfolio.service;

import com.ialmeida.marketpulse.portfolio.dto.CreatePositionRequest;
import com.ialmeida.marketpulse.portfolio.dto.PositionResponse;
import com.ialmeida.marketpulse.portfolio.dto.UpdatePositionRequest;
import com.ialmeida.marketpulse.portfolio.exception.PortfolioNotFoundException;
import com.ialmeida.marketpulse.portfolio.exception.PositionNotFoundException;
import com.ialmeida.marketpulse.portfolio.model.Position;
import com.ialmeida.marketpulse.portfolio.model.Portfolio;
import com.ialmeida.marketpulse.portfolio.repository.PositionRepository;
import com.ialmeida.marketpulse.portfolio.repository.PortfolioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PositionService {

    private final PositionRepository positionRepository;
    private final PortfolioRepository portfolioRepository;

    public PositionService(
            PositionRepository positionRepository,
            PortfolioRepository portfolioRepository
    ) {
        this.positionRepository = positionRepository;
        this.portfolioRepository = portfolioRepository;
    }

    public PositionResponse createPosition(
            Long portfolioId,
            CreatePositionRequest request
    ) {

        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new PortfolioNotFoundException(portfolioId));

        Position position = new Position(
                portfolio,
                request.getSymbol(),
                request.getQuantity(),
                request.getAverageCost(),
                request.getCurrency()
        );

        Position savedPosition = positionRepository.save(position);

        return toResponse(savedPosition);
    }

    public List<PositionResponse> getPositions(Long portfolioId) {

        if (!portfolioRepository.existsById(portfolioId)) {
            throw new PortfolioNotFoundException(portfolioId);
        }

        return positionRepository
                .findByPortfolioId(portfolioId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public PositionResponse getPosition(
            Long portfolioId,
            Long positionId
    ) {

        if (!portfolioRepository.existsById(portfolioId)) {
            throw new PortfolioNotFoundException(portfolioId);
        }

        Position position = positionRepository
                .findByIdAndPortfolioId(positionId, portfolioId)
                .orElseThrow(() -> new PositionNotFoundException(positionId));

        return toResponse(position);
    }

    public PositionResponse updatePosition(
            Long portfolioId,
            Long positionId,
            UpdatePositionRequest request
    ) {
        if (!portfolioRepository.existsById(portfolioId)) {
            throw new PortfolioNotFoundException(portfolioId);
        }

        Position position = positionRepository
                .findByIdAndPortfolioId(positionId, portfolioId)
                .orElseThrow(() -> new PositionNotFoundException(positionId));

        position.update(
                request.getSymbol(),
                request.getQuantity(),
                request.getAverageCost(),
                request.getCurrency()
        );

        Position updatedPosition = positionRepository.save(position);

        return toResponse(updatedPosition);
    }

    public void deletePosition(
            Long portfolioId,
            Long positionId
    ) {

        if (!portfolioRepository.existsById(portfolioId)) {
            throw new PortfolioNotFoundException(portfolioId);
        }

        Position position = positionRepository
                .findByIdAndPortfolioId(positionId, portfolioId)
                .orElseThrow(() -> new PositionNotFoundException(positionId));

        positionRepository.delete(position);
    }

    private PositionResponse toResponse(Position position) {

        return new PositionResponse(
                position.getId(),
                position.getSymbol(),
                position.getQuantity().toString(),
                position.getAverageCost().toString(),
                position.getCurrency()
        );
    }
}