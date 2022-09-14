package study.stock.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.stock.domain.Stock;

public interface StockRepository extends JpaRepository<Stock, Long> {
}
