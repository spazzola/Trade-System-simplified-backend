package tradesystemsimplified.supplier;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupplierDao extends JpaRepository<Supplier, Long> {

    Supplier findByName(String name);

}