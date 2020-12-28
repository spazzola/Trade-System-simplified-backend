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


    @Query(value = "SELECT * FROM invoices " +
            "WHERE buyer_fk = ?1 AND is_paid = false AND value > 0 AND to_equalize_negative_invoice != true",
            nativeQuery = true)
    Optional<List <Invoice>> getBuyerNotPaidInvoices(Long buyerId);


    @Query(value = "SELECT * FROM invoices " +
            "WHERE buyer_fk IS NOT NULL AND is_paid = false AND amount_to_use > 0 AND to_equalize_negative_invoice != true",
            nativeQuery = true)
    Optional<List <Invoice>> getBuyersNotPaidInvoices();


    @Query(value = "SELECT * FROM invoices " +
            "WHERE comment LIKE :firstPart AND comment LIKE :secondPart",
            nativeQuery = true)
    Invoice getPrePaidInvoiceReducedByNegativeInvoice(@Param("firstPart") String firstPart,
                                                      @Param("secondPart") String secondPart);

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

    @Query(value = "SELECT * FROM invoices " +
            "INNER JOIN buyers ON invoices.buyer_fk = buyers.buyer_id " +
            "WHERE invoices.buyer_fk = ?1 AND is_used = false AND is_paid = true AND is_created_to_order = false",
            nativeQuery = true)
    List<Invoice> getBuyerNotUsedInvoices(Long buyerId);


    @Query(value = "SELECT * FROM invoices " +
            "INNER JOIN buyers ON invoices.buyer_fk = buyers.buyer_id " +
            "WHERE invoices.buyer_fk = ?1 AND amount_to_use < 0 AND is_used = false",
            nativeQuery = true)
    Optional<Invoice> getBuyerNegativeInvoice(Long buyerId);


    @Query(value = "SELECT * FROM invoices " +
            "WHERE buyer_fk IS NOT null AND is_paid = true AND is_used = false AND amount_to_use > 0",
            nativeQuery = true)
    Optional<List <Invoice>> getBuyersPaidNotUsedPositiveInvoices();


    @Query(value = "SELECT * FROM invoices " +
            "WHERE supplier_fk IS NOT null AND is_paid = true AND is_used = false AND amount_to_use > 0",
            nativeQuery = true)
    Optional<List <Invoice>> getSuppliersPaidNotUsedPositiveInvoices();


    @Query(value = "SELECT * FROM invoices " +
            "WHERE supplier_fk IS NOT null AND is_used = false AND amount_to_use < 0",
            nativeQuery = true)
    Optional<List <Invoice>> getSuppliersPaidNotUsedNegativeInvoices();



    // =====Month=====


    @Query(value = "SELECT * FROM invoices " +
            "INNER JOIN buyers ON invoices.buyer_fk = buyers.buyer_id " +
            "WHERE is_used = false AND amount_to_use > 0 AND is_paid = true " +
            "AND MONTH(invoices.date) = ?1 AND YEAR(invoices.date) = ?2",
            nativeQuery = true)
    Optional<List<Invoice>> getBuyersMonthNotUsedPositivesInvoices(int month, int year);


    @Query(value = "SELECT * FROM invoices " +
            "INNER JOIN buyers ON invoices.buyer_fk = buyers.buyer_id " +
            "WHERE value > 0 AND buyer_fk IS NOT NULL AND is_paid = false AND is_created_to_order = false " +
            "AND MONTH(invoices.date) = ?1 AND YEAR(invoices.date) = ?2",
            nativeQuery = true)
    Optional<List<Invoice>> getBuyersMonthNotPaidInvoicesNotCreatedToOrder(int month, int year);


    @Query(value = "SELECT * FROM invoices " +
            "INNER JOIN buyers ON invoices.buyer_fk = buyers.buyer_id " +
            "WHERE value > 0 AND buyer_fk IS NOT NULL AND is_paid = false AND is_created_to_order = true " +
            "AND MONTH(invoices.date) = ?1 AND YEAR(invoices.date) = ?2",
            nativeQuery = true)
    Optional<List<Invoice>> getBuyersMonthNotPaidInvoicesCreatedToOrder(int month, int year);



    // =====Year=====

    @Query(value = "SELECT * FROM invoices " +
            "INNER JOIN buyers ON invoices.buyer_fk = buyers.buyer_id " +
            "WHERE value > 0 AND buyer_fk IS NOT NULL AND is_paid = false AND is_created_to_order = true " +
            "AND YEAR(invoices.date) = ?1",
            nativeQuery = true)
    Optional<List<Invoice>> getBuyersYearNotPaidInvoicesCreatedToOrder(int year);

    @Query(value = "SELECT * FROM invoices " +
            "INNER JOIN buyers ON invoices.buyer_fk = buyers.buyer_id " +
            "WHERE value > 0 AND buyer_fk IS NOT NULL AND is_paid = false AND is_created_to_order = false " +
            "AND YEAR(invoices.date) = ?1",
            nativeQuery = true)
    Optional<List<Invoice>> getBuyersYearNotPaidInvoicesNotCreatedToOrder(int year);

    /***
     *
     * ======================= SUPPLIER =======================
     *
     */


    @Query(value = "SELECT * FROM invoices " +
            "WHERE is_used = true AND value > 0 AND supplier_fk = ?1 " +
            "ORDER BY invoice_id desc " +
            "LIMIT 1",
            nativeQuery = true)
    Invoice getLastUsedSupplierInvoice(Long id);


    @Query(value = "SELECT * FROM invoices " +
            "WHERE is_used = false AND amount_to_use > 0 AND supplier_fk = ?1 " +
            "ORDER BY invoice_id desc " +
            "LIMIT 1",
            nativeQuery = true)
    Invoice getSupplierCurrentlyUsingInvoice(Long id);


    @Query(value = "SELECT * FROM invoices " +
            "INNER JOIN suppliers ON invoices.supplier_fk = suppliers.supplier_id " +
            "WHERE invoices.supplier_fk = ?1 AND is_used = false AND is_paid = true",
            nativeQuery = true)
    List<Invoice> getSupplierNotUsedInvoices(Long supplierId);


    @Query(value = "SELECT * FROM invoices " +
            "INNER JOIN suppliers ON invoices.supplier_fk = suppliers.supplier_id " +
            "WHERE invoices.supplier_fk = ?1 AND amount_to_use < 0 AND is_used = false",
            nativeQuery = true)
    Optional<Invoice> getSupplierNegativeInvoice(Long supplierId);


    // =====Month=====


    @Query(value = "SELECT * FROM invoices i " +
            "WHERE supplier_fk = ?1 AND MONTH(i.date) = ?2 AND YEAR(i.date) = ?3 AND value > 0",
            nativeQuery = true)
    List<Invoice> getSupplierMonthInvoices(Long supplierId, int month, int year);


    @Query(value = "SELECT * FROM invoices i " +
            "WHERE supplier_fk IS NOT NULL AND MONTH(i.date) = ?1 AND YEAR(i.date) = ?2 AND value > 0",
            nativeQuery = true)
    List<Invoice> getSuppliersMonthInvoices(int month, int year);


    @Query(value = "SELECT * FROM invoices " +
            "INNER JOIN suppliers ON invoices.supplier_fk = suppliers.supplier_id " +
            "WHERE is_used = false AND amount_to_use > 0 " +
            "AND MONTH(invoices.date) = ?1 AND YEAR(invoices.date) = ?2 " +
            "AND is_paid = true",
            nativeQuery = true)
    Optional<List<Invoice>> getSuppliersMonthNotUsedInvoices(int month, int year);

}

