package study.stock.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import study.stock.domain.Stock;
import study.stock.repository.StockRepository;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class StockServiceTest {

    @Autowired
    private StockService stockService;

    @Autowired
    private StockRepository stockRepository;

    @BeforeEach
    public void before() {
        Stock stock = new Stock(1L, 100L);
        stockRepository.saveAndFlush(stock);
    }

    @AfterEach
    public void after() {
        stockRepository.deleteAll();
    }

    @Test
    public void stock_decrease() {
        stockService.decrease(1L, 1L);
        // 100 - 1 = 99
        Stock stock = stockRepository.findById(1L).orElseThrow();
        assertEquals(99, stock.getQuantity());
    }

    @Test
    public void 동시에_100개_요청() throws InterruptedException {
        int threadCount = 100;
        // Excutors Service -> 비동기 실행하는 작업을 단순화하여 사용할수있게 도와주는 자바 API
        ExecutorService executorService = Executors.newFixedThreadPool(32);

        // 100개의 요청이 끝날때까지 기다려야 하므로 CountDownLatch 사용
        // CountDownLatch : 다른 쓰레드에서 수행중인 작업이 완료될때까지 대기할수 있도록 도와주는 Class
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try{
                    stockService.decrease(1L, 1L);
                }finally {
                    latch.countDown();
                }
            });
        }
        latch.await();

        Stock stock = stockRepository.findById(1L).orElseThrow();

        // 기대 : 100 - (1*100) = 0
        assertEquals(0, stock.getQuantity());

        // result
        // 기대와 다르게 실패 : 1갸의 thread 에 여러개의 data 접근가능하기때문
        // 해결방법 -> 1개의 thread 에 한개의 data 만 접근 가능하도록 -> synchronized 예약어 추가
        // -> 실패 :  @Transactional 어노테이션 동작방식 때문
        // -> 스프링은 @Transactional 이 붙은 클래스 새로 만들어서 실행함
        // -> 트랜잭션 종료시점에 db update 실행하는데 그전에 다른 thread 가 db 접근하는 경우 동시성 이슈 발생
        // -> @Transactional 어노테이션 제거하고 synchronized 예약어만 추가하고 실행
        //      -> 테스트 성공

    }
}