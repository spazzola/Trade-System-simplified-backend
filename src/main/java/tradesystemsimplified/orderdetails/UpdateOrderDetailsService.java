package tradesystemsimplified.orderdetails;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tradesystemsimplified.buyer.Buyer;
import tradesystemsimplified.buyer.BuyerDao;
import tradesystemsimplified.invoice.Invoice;
import tradesystemsimplified.invoice.InvoiceDao;
import tradesystemsimplified.order.*;
import tradesystemsimplified.payment.Payment;
import tradesystemsimplified.payment.PaymentDao;
import tradesystemsimplified.price.PriceDao;
import tradesystemsimplified.price.pricehistory.PriceHistoryDao;
import tradesystemsimplified.product.Product;
import tradesystemsimplified.product.ProductDto;
import tradesystemsimplified.supplier.Supplier;
import tradesystemsimplified.supplier.SupplierDao;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class UpdateOrderDetailsService {


    private OrderService orderService;
    private OrderDetailsService orderDetailsService;
    private PaymentDao paymentDao;
    private InvoiceDao invoiceDao;
    private PriceDao priceDao;
    private OrderDao orderDao;
    private BuyerDao buyerDao;
    private SupplierDao supplierDao;
    private OrderDetailsDao orderDetailsDao;
    private PriceHistoryDao priceHistoryDao;


    @Transactional
    public void updateOrder(UpdateOrderDetailsRequest updateOrderDetailsRequest) {
        OrderDetails orderDetails = orderDetailsService.getOrderById(updateOrderDetailsRequest.getId());
        orderDetails.setTypedSoldPrice(BigDecimal.ZERO);
        orderDetails.setTypedBoughtPrice(BigDecimal.ZERO);
        //Product product = orderDetails.getProduct();
        Buyer oldBuyer = orderDetails.getOrder().getBuyer();
        Supplier oldSupplier = orderDetails.getOrder().getSupplier();

        if (!orderDetails.getTransportNumber().equals(updateOrderDetailsRequest.getNewTransportNumber())) {
            orderDetails.setTransportNumber(updateOrderDetailsRequest.getNewTransportNumber());
        }

        if (updateOrderDetailsRequest.getNewQuantity() == null) {
            updateOrderDetailsRequest.setNewQuantity(orderDetails.getQuantity());
        }
        if (updateOrderDetailsRequest.getNewBuyerPrice() == null) {
            BigDecimal previousPrice = calculatePreviousPrice(orderDetails, orderDetails.getBuyerSum());
            updateOrderDetailsRequest.setNewBuyerPrice(previousPrice);
        }

        if (updateOrderDetailsRequest.getNewSupplierPrice() == null) {
            BigDecimal previousPrice = calculatePreviousPrice(orderDetails, orderDetails.getSupplierSum());
            updateOrderDetailsRequest.setNewSupplierPrice(previousPrice);
        }

        if (checkIfMerchantIsChanged(updateOrderDetailsRequest, oldBuyer, oldSupplier)) {
            processNewOrder(updateOrderDetailsRequest, orderDetails);
        } else {
            updateBuyerOrder(updateOrderDetailsRequest, orderDetails);
            updateSupplierOrder(updateOrderDetailsRequest, orderDetails);
        }
    }

    @Transactional
    public void deleteOrder(Long id) {
        Order order = orderDao.findById(id)
                .orElseThrow(RuntimeException::new);

        processDeletingOrder(order.getOrderDetails().get(0));
    }

    private boolean checkIfMerchantIsChanged(UpdateOrderDetailsRequest updateOrderDetailsRequest,
                                             Buyer oldBuyer, Supplier oldSupplier) {

        Buyer newBuyer = updateOrderDetailsRequest.getNewBuyer();
        Supplier newSupplier = updateOrderDetailsRequest.getNewSupplier();

        return !newBuyer.getName().equals(oldBuyer.getName()) || !newSupplier.getName().equals(oldSupplier.getName());
    }

    private void processNewOrder(UpdateOrderDetailsRequest updateOrderDetailsRequest, OrderDetails orderDetails) {
        processDeletingOrder(orderDetails);
        createNewOrder(updateOrderDetailsRequest, orderDetails);
    }

    private void createNewOrder(UpdateOrderDetailsRequest updateOrderDetailsRequest, OrderDetails orderDetails) {
        CreateOrderRequest createOrderRequest = new CreateOrderRequest();
        createOrderRequest.setBuyerId(updateOrderDetailsRequest.getNewBuyer().getId());
        createOrderRequest.setSupplierId(updateOrderDetailsRequest.getNewSupplier().getId());
        createOrderRequest.setDate(orderDetails.getOrder().getDate());

        ProductDto productDto = new ProductDto();
        productDto.setId(orderDetails.getProduct().getId());

        List<OrderDetailsDto> newOrderDetails = new ArrayList<>();
        OrderDetailsDto orderDetailsDto = new OrderDetailsDto();
        orderDetailsDto.setProduct(productDto);
        orderDetailsDto.setQuantity(updateOrderDetailsRequest.getNewQuantity());
        orderDetailsDto.setTransportNumber(updateOrderDetailsRequest.getNewTransportNumber());
        orderDetailsDto.setTypedSoldPrice(BigDecimal.ZERO);
        orderDetailsDto.setTypedBoughtPrice(BigDecimal.ZERO);
        orderDetailsDto.setCreateBuyerInvoice(orderDetails.isCreateBuyerInvoice());
        orderDetailsDto.setInvoiceNumber(orderDetails.getInvoiceNumber());

        newOrderDetails.add(orderDetailsDto);
        createOrderRequest.setOrderDetails(newOrderDetails);

        orderService.createOrder(createOrderRequest);
    }

    private void processDeletingOrder(OrderDetails orderDetails) {
        deletePayment(orderDetails);
        deleteBuyerInvoice(orderDetails);

        returnMoneyToBuyer(orderDetails);
        returnMoneyToSupplier(orderDetails);

        orderDetailsDao.delete(orderDetails);
        orderDao.delete(orderDetails.getOrder());
    }

    private void deletePayment(OrderDetails orderDetails) {
        Optional<Payment> payment = paymentDao.findBuyerPayment(orderDetails.getId());
        payment.ifPresent(payment1 -> paymentDao.delete(payment1));
    }

    private void deleteBuyerInvoice(OrderDetails orderDetails) {
        if (orderDetails.isCreateBuyerInvoice()) {
            Invoice invoice = invoiceDao.getByInvoiceNumber(orderDetails.getInvoiceNumber());
            invoiceDao.delete(invoice);
        }
    }

    private void returnMoneyToBuyer(OrderDetails orderDetails) {
        Buyer buyer = orderDetails.getOrder().getBuyer();
        BigDecimal currentBalance = buyer.getCurrentBalance();
        currentBalance = currentBalance.add(orderDetails.getBuyerSum());
        buyer.setCurrentBalance(currentBalance);
        buyerDao.save(buyer);
    }

    private void returnMoneyToSupplier(OrderDetails orderDetails) {
        Supplier supplier = orderDetails.getOrder().getSupplier();
        BigDecimal currentBalance = supplier.getCurrentBalance();
        currentBalance = currentBalance.add(orderDetails.getSupplierSum());
        supplier.setCurrentBalance(currentBalance);
        supplierDao.save(supplier);
    }

    private void updateBuyerOrder(UpdateOrderDetailsRequest updateOrderDetailsRequest, OrderDetails orderDetails) {
        updateBuyerBalance(updateOrderDetailsRequest, orderDetails);

        BigDecimal newBuyerSum = updateOrderDetailsRequest.getNewBuyerPrice().multiply(updateOrderDetailsRequest.getNewQuantity());
        orderDetails.setBuyerSum(newBuyerSum);
        orderDetails.setQuantity(updateOrderDetailsRequest.getNewQuantity());
        orderDetailsDao.save(orderDetails);
    }

    private void updateBuyerBalance(UpdateOrderDetailsRequest updateOrderDetailsRequest, OrderDetails orderDetails) {
        BigDecimal oldBuyerSum = orderDetails.getBuyerSum();
        BigDecimal newBuyerSum = updateOrderDetailsRequest.getNewBuyerPrice().multiply(updateOrderDetailsRequest.getNewQuantity());
        BigDecimal difference = oldBuyerSum.subtract(newBuyerSum);
        Buyer buyer = orderDetails.getOrder().getBuyer();
        BigDecimal currentBalance = buyer.getCurrentBalance();

        if (newBuyerSum.compareTo(oldBuyerSum) < 0  && difference.compareTo(BigDecimal.ZERO) > 0) {
            currentBalance = currentBalance.add(difference);
        }
        else if (newBuyerSum.compareTo(oldBuyerSum) > 0  && difference.compareTo(BigDecimal.ZERO) < 0) {
            currentBalance = currentBalance.add(difference);
        }
        else {
            currentBalance = currentBalance.subtract(difference);
        }

        buyer.setCurrentBalance(currentBalance);
        buyerDao.save(buyer);

        if (orderDetails.isCreateBuyerInvoice()) {
            updateBuyerInvoice(orderDetails, difference);
        }
    }

    private void updateBuyerInvoice(OrderDetails orderDetails, BigDecimal difference) {
        Invoice invoice = invoiceDao.getByInvoiceNumber(orderDetails.getInvoiceNumber());
        BigDecimal currentValue = invoice.getValue();

        if (difference.compareTo(BigDecimal.ZERO) < 0) {
            currentValue = currentValue.add(difference);
        }
        if (difference.compareTo(BigDecimal.ZERO) > 0) {
            currentValue = currentValue.subtract(difference);
        }
        invoice.setValue(currentValue);
        invoiceDao.save(invoice);
    }

    private void updateSupplierOrder(UpdateOrderDetailsRequest updateOrderDetailsRequest, OrderDetails orderDetails) {
        updateSupplierBalance(updateOrderDetailsRequest, orderDetails);

        BigDecimal newSupplierSum = updateOrderDetailsRequest.getNewSupplierPrice().multiply(updateOrderDetailsRequest.getNewQuantity());
        orderDetails.setSupplierSum(newSupplierSum);
        orderDetailsDao.save(orderDetails);
    }

    private void updateSupplierBalance(UpdateOrderDetailsRequest updateOrderDetailsRequest, OrderDetails orderDetails) {
        BigDecimal oldSupplierSum = orderDetails.getSupplierSum();
        BigDecimal newSupplierSum = updateOrderDetailsRequest.getNewSupplierPrice().multiply(updateOrderDetailsRequest.getNewQuantity());
        BigDecimal difference = oldSupplierSum.subtract(newSupplierSum);
        Supplier supplier = orderDetails.getOrder().getSupplier();
        BigDecimal currentBalance = supplier.getCurrentBalance();

        if (newSupplierSum.compareTo(oldSupplierSum) < 0  && difference.compareTo(BigDecimal.ZERO) > 0) {
            currentBalance = currentBalance.add(difference);
        }
        else if (newSupplierSum.compareTo(oldSupplierSum) > 0  && difference.compareTo(BigDecimal.ZERO) < 0) {
            currentBalance = currentBalance.add(difference);
        }
        else {
            currentBalance = currentBalance.subtract(difference);
        }

        supplier.setCurrentBalance(currentBalance);
        supplierDao.save(supplier);
    }

    private BigDecimal calculatePreviousPrice(OrderDetails orderDetails, BigDecimal merchantSum) {
        BigDecimal quantity = orderDetails.getQuantity();
        return merchantSum.divide(quantity, RoundingMode.HALF_EVEN);
    }

}