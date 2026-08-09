package com.ialmeida.marketpulse.portfolio.repository;

import com.ialmeida.marketpulse.portfolio.model.Position;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PositionRepository extends JpaRepository<Position, Long> {

    List<Position> findByPortfolioId(Long portfolioId);

    Optional<Position> findByIdAndPortfolioId(
            Long positionId,
            Long portfolioId
    );
}