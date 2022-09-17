package study.stock.facade;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import study.stock.repository.RedisLockRepository;
import study.stock.service.StockService;

/**
 *  lettuceLock
 *  - 장점 : 구현이 간단
 *  - 주의점 : 스핀락방식이므로 redis 부하를 줄수 있다
 *  -> 따라서 thread.sleep(100) 을 통해 부하 감소 로직 추가
 */
@Component
@RequiredArgsConstructor
public class LettuceLockStockFacade {

    private final RedisLockRepository redisLockRepository;

    private final StockService stockService;

    public void decrease(Long key, Long quantity) throws InterruptedException {

        while (!redisLockRepository.lock(key)) {    // 락 획득 시도
            Thread.sleep(100);  // 락 획득 실패시 Thread.sleep(100) 으로 Redis 부하 감소
        }
        // 락 획득에 성공한 경우
        try{
            stockService.decrease(key, quantity);   // 재고 감소
        }finally {
            redisLockRepository.unlock(key);    // 재고 감소 처리 후 락 반환
        }
    }


}
