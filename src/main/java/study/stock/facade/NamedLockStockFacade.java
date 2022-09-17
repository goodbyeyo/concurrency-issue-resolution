package study.stock.facade;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import study.stock.repository.LockRepository;
import study.stock.service.StockService;

/**
 * 네임드 락은 보통 분산락을 구현할때 사용
 * PessimisticLock : TimeOut 구현하기 힘들다
 * NamedLock
 *  - 장점 TimeOut 구현 쉽다, 데이터 삽입 시 데이터 정합성을 맞춰야 하는 경우 사용 할 수 있다
 *  - 주의점 : Lock 해제와 Session 관리를 잘해줘야 한다, 실제로 사용시 구현방법이 복잡할수 있다
 *  P
 */
@Component
@RequiredArgsConstructor
public class NamedLockStockFacade {

    private final LockRepository lockRepository;
    private final StockService stockService;

    public void decrease(Long id, Long quantity) {
        try{
            lockRepository.getLock(id.toString());
            stockService.decrease(id, quantity);
        }finally {
            lockRepository.releaseLock(id.toString());
        }
    }

}
