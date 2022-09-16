package study.stock.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import study.stock.domain.Stock;

import javax.persistence.LockModeType;

public interface StockRepository extends JpaRepository<Stock, Long> {

    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from Stock as s where s.id = :id")
    Stock findByIdWithPessimisticLock(Long id);

    @Lock(value = LockModeType.OPTIMISTIC)
    @Query("select s from Stock as s where s.id = :id")
    Stock findByIdWithOptimisticLock(Long id);
}