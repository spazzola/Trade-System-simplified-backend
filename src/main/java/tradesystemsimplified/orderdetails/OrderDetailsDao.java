package tradesystemsimplified.orderdetails;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("orderDetailsDao")
public interface OrderDetailsDao extends JpaRepository<OrderDetails, Long> {

}
