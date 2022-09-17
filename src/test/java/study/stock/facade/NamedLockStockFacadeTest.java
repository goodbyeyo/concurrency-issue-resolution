package study.stock.facade;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import study.stock.domain.Stock;
import study.stock.repository.StockRepository;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class NamedLockStockFacadeTest {
    @Autowired
    private NamedLockStockFacade namedLockStockFacade;

    @Autowired
    private StockRepository stockRepository;

    @BeforeEach
    public void insert() {
        Stock stock = new Stock(1L, 100L);
        stockRepository.saveAndFlush(stock);
    }

    @AfterEach
    public void delete() {
        stockRepository.deleteAll();
    }


    @Test
    public void 동시에_100개_요청_with_NamedLock() throws InterruptedException {
        int threadCount = 100;
        // Excutors Service -> 비동기 실행하는 작업을 단순화하여 사용할수있게 도와주는 자바 API
        ExecutorService executorService = Executors.newFixedThreadPool(32);

        // 100개의 요청이 끝날때까지 기다려야 하므로 CountDownLatch 사용
        // CountDownLatch : 다른 쓰레드에서 수행중인 작업이 완료될때까지 대기할수 있도록 도와주는 Class
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try{
                    namedLockStockFacade.decrease(1L, 1L);
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        Stock stock = stockRepository.findById(1L).orElseThrow();
        // 기대 : 100 - (1*100) = 0
        assertEquals(0, stock.getQuantity());

    }
}