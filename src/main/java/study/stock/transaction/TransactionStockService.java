package study.stock.transaction;

import lombok.RequiredArgsConstructor;
import study.stock.service.StockService;

@RequiredArgsConstructor
public class TransactionStockService {
    private StockService stockService;

    public void decrease(Long id, Long quantity) {
        startTransaction();

        stockService.decrease(id, quantity);

        endTransaction();
        // 트랜잭션 종료시점에 database 업데이트 실행함
        // 실제로 database 업데이트가 완료되기전에 다른 thread 가 decrease() 를 호출할수 있음
        // 다른 thread 는 갱신되기전에 값을 가져와서 동시성 문제가 발생함
   }

    private void endTransaction() {
    }

    private void startTransaction() {

    }

}
