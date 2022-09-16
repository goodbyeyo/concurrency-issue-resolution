package study.stock.facade;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import study.stock.service.OptimisticLockStockService;

@Service
@RequiredArgsConstructor
public class OptimisticLockStockFacade {

    private final OptimisticLockStockService optimisticLockStockService;

    public void decrease(Long id, Long quantity) throws InterruptedException {
        while (true) {
            try {
                optimisticLockStockService.decrease(id, quantity);
                break;
            } catch (Exception e) {
                Thread.sleep(50);
            }
        }

    }

}
