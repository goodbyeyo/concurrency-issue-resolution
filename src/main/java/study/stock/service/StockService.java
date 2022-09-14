package study.stock.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import study.stock.domain.Stock;
import study.stock.repository.StockRepository;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;

    public void decrease(Long id, Long quantity) {
        // 1. 재고 가져와서
        // 2. 수량 감소
        // 3. DB 저장
        Stock stock = stockRepository.findById(id).orElseThrow();
        stock.decrease(quantity);
        stockRepository.saveAndFlush(stock);
    }
}
