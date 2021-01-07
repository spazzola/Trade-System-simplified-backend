package tradesystemsimplified.invoice;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository("invoiceDao")
public interface InvoiceDao extends JpaRepository<Invoice, Long> {


    @Query(value = "SELECT * FROM invoices " +
            "WHERE MONTH(invoices.date) = ?1 AND YEAR(invoices.date) = ?2 AND value > 0",
            nativeQuery = true)
    List<Invoice> getMonthInvoices(int month, int year);


    @Query(value = "SELECT * FROM invoices " +
            "WHERE invoice_number = ?1 AND is_paid = false",
            nativeQuery = true)
    Invoice getByInvoiceNumber(String invoiceNumber);



    /***
     *
     * ======================= BUYER =======================
     *
     */

    @Query(value = "SELECT * FROM invoices i " +
            "WHERE buyer_fk = ?1 AND MONTH(i.date) = ?2 AND YEAR(i.date) = ?3 AND value > 0",
            nativeQuery = true)
    List<Invoice> getBuyerMonthInvoices(Long buyerId, int month, int year);

    @Query(value = "SELECT * FROM invoices i " +
            "WHERE buyer_fk IS NOT NULL AND MONTH(i.date) = ?1 AND YEAR(i.date) = ?2 AND value > 0",
            nativeQuery = true)
    List<Invoice> getBuyersMonthInvoices(int month, int year);


    // =====Month=====

    @Query(value = "SELECT * FROM invoices " +
            "WHERE MONTH(i.date) = ?§ AND YEAR(i.date) = ?2 " +
            "AND is_paid = false AND buyer_fk IS NOT NULL",
           nativeQuery = true)
    Optional<List<Invoice>> getBuyerMonthNotPaidInvoices(int month, int year);


    // =====Year=====

    @Query(value = "SELECT * FROM invoices " +
            "WHERE YEAR(i.date) = ?1 " +
            "AND is_paid = false AND buyer_fk IS NOT NULL",
            nativeQuery = true)
    Optional<List<Invoice>> getBuyerYearNotPaidInvoices(int year);

    /***
     *
     * ======================= SUPPLIER =======================
     *
     */


    // =====Month=====


    @Query(value = "SELECT * FROM invoices i " +
            "WHERE supplier_fk = ?1 AND MONTH(i.date) = ?2 AND YEAR(i.date) = ?3 AND value > 0",
            nativeQuery = true)
    List<Invoice> getSupplierMonthInvoices(Long supplierId, int month, int year);


    @Query(value = "SELECT * FROM invoices i " +
            "WHERE supplier_fk IS NOT NULL AND MONTH(i.date) = ?1 AND YEAR(i.date) = ?2 AND value > 0",
            nativeQuery = true)
    List<Invoice> getSuppliersMonthInvoices(int month, int year);

}