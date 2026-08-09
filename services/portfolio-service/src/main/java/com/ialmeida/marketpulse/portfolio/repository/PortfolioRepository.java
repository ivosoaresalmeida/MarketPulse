package com.ialmeida.marketpulse.portfolio.repository;

import com.ialmeida.marketpulse.portfolio.model.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {

}