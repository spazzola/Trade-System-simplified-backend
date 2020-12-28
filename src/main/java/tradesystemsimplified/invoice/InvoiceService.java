package tradesystemsimplified.invoice;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tradesystemsimplified.buyer.Buyer;
import tradesystemsimplified.buyer.BuyerDao;
import tradesystemsimplified.buyer.BuyerDto;
import tradesystemsimplified.supplier.Supplier;
import tradesystemsimplified.supplier.SupplierDao;
import tradesystemsimplified.supplier.SupplierDto;

import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.List;


@AllArgsConstructor
@Service
public class InvoiceService {

    private InvoiceDao invoiceDao;
    private BuyerDao buyerDao;
    private SupplierDao supplierDao;


    @Transactional
    public Invoice createInvoice(InvoiceDto invoiceDto) {
        if (validateInvoice(invoiceDto)) {
            Buyer buyer = null;
            final BuyerDto buyerDto = invoiceDto.getBuyer();
            if (buyerDto != null) {
                buyer = buyerDao.findById(buyerDto.getId())
                        .orElseThrow(RuntimeException::new);
            }

            Supplier supplier = null;
            final SupplierDto supplierDto = invoiceDto.getSupplier();
            if (supplierDto != null) {
                supplier = supplierDao.findById(supplierDto.getId())
                        .orElseThrow(RuntimeException::new);
            }

            final Invoice invoice = Invoice.builder()
                    .id(null)
                    .invoiceNumber(invoiceDto.getInvoiceNumber())
                    .date(invoiceDto.getDate())
                    .value(invoiceDto.getValue())
                    .isPaid(invoiceDto.isPaid())
                    .comment(invoiceDto.getComment())
                    .buyer(buyer)
                    .supplier(supplier)
                    .build();

            recalculateBalances(invoice, buyer, supplier);

            return invoiceDao.save(invoice);
        } else {
            throw new RuntimeException("Nie można stworzyć faktury");
        }
    }

    @Transactional
    public Invoice getInvoice(Long id) {
        Optional<Invoice> invoice = invoiceDao.findById(id);

        return invoice
                .orElseThrow(NoSuchElementException::new);
    }

    @Transactional
    public List<Invoice> getAll() {
        return invoiceDao.findAll();
    }

    @Transactional
    public List<Invoice> getInvoicesByMonth(int month, int year) {
        return invoiceDao.getMonthInvoices(month, year);
    }

    @Transactional
    public Invoice getInvoiceByInvoiceNumber(String invoiceNumber) {
        return invoiceDao.getByInvoiceNumber(invoiceNumber);
    }

    //TODO need to change method body-logic
    @Transactional
    public void payForInvoice(Long id) {
        Optional<Invoice> optionalInvoice = invoiceDao.findById(id);
        Invoice invoice = optionalInvoice
                .orElseThrow(NoSuchElementException::new);

        recalculateBalances(invoice, invoice.getBuyer(), invoice.getSupplier());
    }

    @Transactional
    public List<Invoice> getBuyerMonthInvoices(Long buyerId, int month, int year) {
        return invoiceDao.getBuyerMonthInvoices(buyerId, month, year);
    }

    @Transactional
    public List<Invoice> getBuyersMonthInvoices(int month, int year) {
        return invoiceDao.getBuyersMonthInvoices(month, year);
    }

    @Transactional
    public List<Invoice> getSupplierMonthInvoices(Long supplierId, int month, int year) {
        return invoiceDao.getSupplierMonthInvoices(supplierId, month, year);
    }

    @Transactional
    public List<Invoice> getSuppliersMonthInvoices(int month, int year) {
        return invoiceDao.getSuppliersMonthInvoices(month, year);
    }

    private void recalculateBalances(Invoice invoice, Buyer buyer, Supplier supplier) {
        if (invoice.isPaid()) {
            if (buyer != null) {
                recalculateBuyerBalance(invoice, buyer);
            } else {
                recalculateSupplierBalance(invoice, supplier);
            }
        }
    }

    private void recalculateBuyerBalance(Invoice invoice, Buyer buyer) {
        BigDecimal currentBalance = buyer.getCurrentBalance();
        BigDecimal newBalance = currentBalance.add(invoice.getValue());
        buyer.setCurrentBalance(newBalance);
    }

    private void recalculateSupplierBalance(Invoice invoice, Supplier supplier) {
        BigDecimal currentBalance = supplier.getCurrentBalance();
        BigDecimal newBalance = currentBalance.add(invoice.getValue());
        supplier.setCurrentBalance(newBalance);
    }

    private boolean validateInvoice(InvoiceDto invoiceDto) {
        if (invoiceDto.getInvoiceNumber() == null || invoiceDto.getInvoiceNumber().equals("")) {
            return false;
        }
        if (invoiceDto.getDate() == null) {
            return false;
        }
        if (invoiceDto.getValue().doubleValue() <= 0) {
            return false;
        }
        if (invoiceDto.getBuyer() != null && invoiceDto.getSupplier() != null) {
            throw new RuntimeException("Nie mozna stworzyc faktury dla buyera i suppliera naraz");
        }
        if (invoiceDto.getBuyer() == null && invoiceDto.getSupplier() == null) {
            throw new RuntimeException("Nie mozna stworzyc faktury bez buyera lub suppliera");
        }
        return true;
    }

}