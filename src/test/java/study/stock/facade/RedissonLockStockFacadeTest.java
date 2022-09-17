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
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RedissonLockStockFacadeTest {

    @Autowired
    private RedissonLockStockFacade redissonLockStockFacade;

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
    public void 동시에_100개_요청_with_lettuceLock() throws InterruptedException {
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

        IntStream.range(0, threadCount)
                .<Runnable>mapToObj(i -> () -> {
                    try {
                        redissonLockStockFacade.decrease(1L, 1L);
                    } finally {
                        latch.countDown();
                    }
                 }).forEach(executorService::submit);
        latch.await();
        Stock stock = stockRepository.findById(1L).orElseThrow();
        // 기대 : 100 - (1*100) = 0
        assertEquals(0, stock.getQuantity());

    }

}