package study.stock.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import study.stock.domain.Stock;

/**
 * 실무에서 NamedLock 을 사용할때는 DataSource 를 분리해서 사용해야한다
 * 즉 별도의 JDBC 를 통해 Connection 을 하는것을 추천
 * 같은 DataSource를 사용하면 ConnectionPool이 부족해져서 다른 서비스에 영향을 미칠수 있다
 */
public interface LockRepository extends JpaRepository<Stock, Long> {
    @Query(value = "select get_lock(:key, 3000)", nativeQuery = true)
    void getLock(String key);

    @Query(value = "select get_release(:key)", nativeQuery = true)
    void releaseLock(String key);

}
