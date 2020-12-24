package tradesystemsimplified.price.pricehistory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PriceHistoryDao  extends JpaRepository<PriceHistory, Long> {

    List<PriceHistory> findByBuyerId(Long buyerId);

}
