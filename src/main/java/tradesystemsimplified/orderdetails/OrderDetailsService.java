package tradesystemsimplified.orderdetails;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tradesystemsimplified.buyer.Buyer;
import tradesystemsimplified.buyer.BuyerDao;
import tradesystemsimplified.invoice.Invoice;
import tradesystemsimplified.invoice.InvoiceDao;
import tradesystemsimplified.payment.Payment;
import tradesystemsimplified.payment.PaymentDao;
import tradesystemsimplified.price.PriceDao;
import tradesystemsimplified.price.pricehistory.PriceHistoryService;
import tradesystemsimplified.product.Product;
import tradesystemsimplified.supplier.Supplier;
import tradesystemsimplified.supplier.SupplierDao;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class OrderDetailsService {


    private PriceDao priceDao;
    private InvoiceDao invoiceDao;
    private OrderDetailsDao orderDetailsDao;
    private PaymentDao paymentDao;
    private BuyerDao buyerDao;
    private SupplierDao supplierDao;
    private PriceHistoryService priceHistoryService;


    @Transactional
    public void calculateOrderDetail(OrderDetails orderDetails) {
        BigDecimal buyerSum = calculateBuyerOrder(orderDetails);
        BigDecimal supplierSum = calculateSupplierOrder(orderDetails);
        orderDetails.setBuyerSum(buyerSum);
        orderDetails.setSupplierSum(supplierSum);
        orderDetailsDao.save(orderDetails);

        if (orderDetails.isCreateBuyerInvoice()) {
            createBuyerInvoice(orderDetails, buyerSum);
        }

        payForBuyerOrder(orderDetails, buyerSum);
        payForSupplierOrder(orderDetails, supplierSum);
    }

    @Transactional
    public OrderDetails getOrderById(Long orderId) {
        return orderDetailsDao.findById(orderId)
                .orElseThrow(RuntimeException::new);
    }

    private BigDecimal calculateBuyerOrder(OrderDetails orderDetails) {
        Long buyerId = orderDetails.getOrder().getBuyer().getId();
        Long productId = orderDetails.getProduct().getId();

        BigDecimal quantity = orderDetails.getQuantity();

        BigDecimal price;

        if (orderDetails.getTypedSoldPrice().doubleValue() > 0) {
            price = orderDetails.getTypedSoldPrice();
        } else {
            price = priceDao.getBuyerPrice(buyerId, productId);
        }

        if (price != null) {
            Buyer buyer = orderDetails.getOrder().getBuyer();
            Supplier supplier = orderDetails.getOrder().getSupplier();
            Product product = orderDetails.getProduct();
            priceHistoryService.createPriceHistory(buyer, supplier, product, price);
            return quantity.multiply(price).setScale(2, RoundingMode.HALF_UP);
        } else {
            throw new RuntimeException("Kupiec nie ma ustawionej ceny dla tego produktu");
        }
    }

    private BigDecimal calculateSupplierOrder(OrderDetails orderDetails) {
        Long supplierId = orderDetails.getOrder().getSupplier().getId();
        Long productId = orderDetails.getProduct().getId();

        BigDecimal quantity = orderDetails.getQuantity();

        BigDecimal price;

        if (orderDetails.getTypedBoughtPrice().doubleValue() > 0) {
            price = orderDetails.getTypedBoughtPrice();
        } else {
            price = priceDao.getSupplierPrice(supplierId, productId);
        }

        if (price != null) {
            return quantity.multiply(price).setScale(2, RoundingMode.HALF_UP);
        } else {
            throw new RuntimeException("Dostawca nie ma ustawionej ceny dla tego produktu");
        }

    }

    private void payForBuyerOrder(OrderDetails orderDetails, BigDecimal buyerSum) {
        Buyer buyer = orderDetails.getOrder().getBuyer();
        BigDecimal currentBalance = buyer.getCurrentBalance();
        buyer.setCurrentBalance(currentBalance.subtract(buyerSum));
        buyerDao.save(buyer);
    }

    private void payForSupplierOrder(OrderDetails orderDetails, BigDecimal supplierSum) {
        Supplier supplier = orderDetails.getOrder().getSupplier();
        BigDecimal currentBalance = supplier.getCurrentBalance();
        supplier.setCurrentBalance(currentBalance.subtract(supplierSum));
        supplierDao.save(supplier);
    }

    private void createBuyerPayment(OrderDetails orderDetails, Invoice invoice) {
        Payment payment = new Payment();
        payment.setOrderDetails(orderDetails);
        payment.setBuyerInvoice(invoice);
        paymentDao.save(payment);
    }

    public void createBuyerInvoice(OrderDetails orderDetails, BigDecimal buyerSum) {
        Invoice invoice = Invoice.builder()
                .buyer(orderDetails.getOrder().getBuyer())
                .date(orderDetails.getOrder().getDate())
                .value(buyerSum)
                .isPaid(false)
                .invoiceNumber(orderDetails.getInvoiceNumber())
                .build();
        invoiceDao.save(invoice);
        createBuyerPayment(orderDetails, invoice);
    }

}