package tradesystemsimplified.payment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentDao extends JpaRepository<Payment, Long> {

    @Query(value = "SELECT * FROM payments " +
            "WHERE order_details_fk = ?1 AND buyer_invoice_fk IS NOT null",
            nativeQuery = true)
    List<Payment> findBuyerPayment(Long OrderDetailsFk);

    @Query(value = "SELECT * FROM payments " +
            "WHERE order_details_fk = ?1 AND supplier_invoice_fk IS NOT null",
            nativeQuery = true)
    List<Payment> findSupplierPayment(Long OrderDetailsFk);

    List<Payment> findByOrderDetailsId(Long id);

    List<Payment> findByBuyerInvoiceId(Long id);

    void deleteByOrderDetailsId(Long id);
}
