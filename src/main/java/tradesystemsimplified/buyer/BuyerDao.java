package tradesystemsimplified.buyer;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BuyerDao extends JpaRepository<Buyer, Long> {

    Buyer findByName(String name);

}