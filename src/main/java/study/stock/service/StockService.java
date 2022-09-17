package study.stock.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import study.stock.domain.Stock;
import study.stock.repository.StockRepository;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;

    /**
     * NamedLock 사용시 synchronized 제거 필요
     * Transactional 어노테이션이 있어야 getLock 과 releaseLock() 이 사용하는 커넥션이 달라진다
     * Propagation.REQUIRES_NEW 사용 이유
     * -> 부모의 트랜잭션과 동일한 범위로 묶인다면 synchronized 와 같은 문제 발생
     * -> 즉 Database 에 commit 되기전에 락이 풀리는 현상 발생
     * -> 따라서 별도의 트랜잭션으로 분리하여 Database 에 정상적으로 commit 된 이후에 락을 해제
     * -> propagation 을 변경하면 새로운 트랜잭션을 시작하기때문에 hikari.maximum-pool-size 의 개수 넉넉히 늘려주는것이 좋다
     * -> 정리하면 lock 을 해제하기전에 Database 에 Commit 이 되도록 하는것
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void decrease(Long id, Long quantity) {
//    public synchronized void decrease(Long id, Long quantity) {
        Stock stock = stockRepository.findById(id).orElseThrow();
        stock.decrease(quantity);
        stockRepository.saveAndFlush(stock);
    }
}
