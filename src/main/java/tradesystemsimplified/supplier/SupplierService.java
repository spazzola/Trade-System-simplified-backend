package tradesystemsimplified.supplier;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tradesystemsimplified.invoice.Invoice;
import tradesystemsimplified.invoice.InvoiceDao;
import tradesystemsimplified.order.Order;
import tradesystemsimplified.order.OrderDao;
import tradesystemsimplified.price.Price;
import tradesystemsimplified.price.PriceDao;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class SupplierService {

    private final SupplierDao supplierDao;
    private final InvoiceDao invoiceDao;
    private final PriceDao priceDao;
    private final OrderDao orderDao;


    @Transactional
    public Supplier createSupplier(SupplierDto supplierDto) {
        if (validateSupplier(supplierDto)) {
            Supplier supplier = Supplier.builder()
                    .name(supplierDto.getName())
                    .build();

            return supplierDao.save(supplier);
        }
        throw new RuntimeException("Can't create supplier");
    }

    @Transactional
    public List<Supplier> getAll() {
        return supplierDao.findAll();
    }

    @Transactional
    public List<Supplier> getBalances() {
        List<Supplier> suppliers = supplierDao.findAll();
        List<Supplier> resultSuppliers = new ArrayList<>();

        calculateCurrentlyTakenQuantity(suppliers);

        for (Supplier supplier : suppliers) {
            supplier = setCurrentBalance(supplier);
            resultSuppliers.add(supplier);
        }

        return resultSuppliers;
    }

    private void calculateCurrentlyTakenQuantity(List<Supplier> suppliers) {
        int month = LocalDate.now().getMonthValue();
        int year = LocalDate.now().getYear();

        for (Supplier supplier : suppliers) {
            List<Order> orders = orderDao.getSupplierMonthOrders(supplier.getId(), month, year);
            BigDecimal sumQuantity = BigDecimal.valueOf(0);

            for (Order order : orders) {
                sumQuantity = sumQuantity.add(order.getOrderDetails().get(0).getQuantity());
            }

            supplier.setCurrentlyTakenQuantity(sumQuantity);
            supplierDao.save(supplier);
        }
    }

    @Transactional
    public List<Supplier> getSuppliersMonthTakenQuantity(int month, int year) {
        List<Supplier> suppliers = supplierDao.findAll();

        for (Supplier supplier : suppliers) {
            List<Order> orders = orderDao.getSupplierMonthOrders(supplier.getId(), month, year);
            BigDecimal sumQuantity = BigDecimal.valueOf(0);

            for (Order order : orders) {
                sumQuantity = sumQuantity.add(order.getOrderDetails().get(0).getQuantity());
            }

            supplier.setMonthTakenQuantity(sumQuantity);
        }
        return suppliers;
    }

    @Transactional
    public List<Price> getSupplierProducts(Long id) {
        return priceDao.getSupplierProducts(id);
    }

    @Transactional
    public Supplier updateSupplierName(String oldSupplierName, String newSupplierName) {
        Supplier buyer = supplierDao.findByName(oldSupplierName);
        buyer.setName(newSupplierName);

        return supplierDao.save(buyer);
    }

    private Supplier setCurrentBalance(Supplier supplier) {

       /* List<Invoice> notUsedInvoices = invoiceDao.getSupplierNotUsedInvoices(supplier.getId());

        BigDecimal balance = BigDecimal.valueOf(0);

        for (Invoice invoice : notUsedInvoices) {
            balance = balance.add(invoice.getAmountToUse());
        }

        Optional<Invoice> negativeInvoice = invoiceDao.getSupplierNegativeInvoice(supplier.getId());
        if (negativeInvoice.isPresent()) {
            balance = balance.add(negativeInvoice.get().getAmountToUse());
        }

        supplier.setCurrentBalance(balance);*/

        return supplier;
    }

    private boolean validateSupplier(SupplierDto supplierDto) {
        if (supplierDto.getName() == null || supplierDto.getName().equals("")) {
            return false;
        }
        Supplier buyer = supplierDao.findByName(supplierDto.getName());
        if (buyer != null) {
            return false;
        }
        return true;
    }

}
