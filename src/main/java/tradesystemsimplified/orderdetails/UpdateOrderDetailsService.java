package tradesystemsimplified.orderdetails;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tradesystemsimplified.buyer.Buyer;
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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Service
public class UpdateOrderDetailsService {
/*

    private OrderService orderService;
    private OrderDetailsService orderDetailsService;
    private PaymentDao paymentDao;
    private InvoiceDao invoiceDao;
    private PriceDao priceDao;
    private OrderDao orderDao;
    private OrderDetailsDao orderDetailsDao;
    private PriceHistoryDao priceHistoryDao;


    @Transactional
    public void updateOrder(UpdateOrderDetailsRequest updateOrderDetailsRequest) {
        OrderDetails orderDetails = orderDetailsService.getOrderById(updateOrderDetailsRequest.getId());
        orderDetails.setTypedSoldPrice(BigDecimal.ZERO);
        orderDetails.setTypedBoughtPrice(BigDecimal.ZERO);
        Product product = orderDetails.getProduct();
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
        List<Payment> payments = paymentDao.findByOrderDetailsId(orderDetails.getId());
        List<Invoice> buyerInvoices = new ArrayList<>();
        List<Invoice> supplierInvoices = new ArrayList<>();

        for (Payment payment : payments) {
            if (payment.getBuyerInvoice() != null) {
                buyerInvoices.add(payment.getBuyerInvoice());
            } else {
                supplierInvoices.add(payment.getSupplierInvoice());
            }
            paymentDao.delete(payment);
        }
        processRecalculatingInvoices(buyerInvoices, orderDetails.getBuyerSum());
        processRecalculatingInvoices(supplierInvoices, orderDetails.getSupplierSum());

        orderDetailsDao.delete(orderDetails);
        orderDao.delete(orderDetails.getOrder());

    }

    private void processRecalculatingInvoices(List<Invoice> invoices, BigDecimal orderValue) {
        if (invoices.size() == 1) {
            processRecalculatingOneInvoice(invoices.get(0), orderValue);
        } else {
            processRecalculatingManyInvoices(invoices, orderValue);
        }
    }

    private void processRecalculatingOneInvoice(Invoice invoice, BigDecimal orderValue) {
        if (invoice.isCreatedToOrder()) {
            invoiceDao.delete(invoice);
        } else if (invoice.getAmountToUse().compareTo(BigDecimal.ZERO) < 0) {
            processRecalculatingNegativeInvoice(invoice, orderValue);
            if (isOrderIsRelatedWithEqualizedInvoice(invoice)) {
                invoice = getBuyerEqualizedInvoice(invoice);
                processRecalculatingEqualizedInvoice(invoice, orderValue);
            }
        } else if (invoice.getAmountToUse().compareTo(BigDecimal.ZERO) == 0 && invoice.getInvoiceNumber().equals("Negatywna")) {
            String firstPart = "Pomniejszono o%";
            String secondPart = "%z faktury o id " + invoice.getId();
            Invoice prePaymentInvoice = invoiceDao.getPrePaidInvoiceReducedByNegativeInvoice(firstPart, secondPart);
            BigDecimal previousAmountToUse = prePaymentInvoice.getAmountToUse();
            prePaymentInvoice.setAmountToUse(previousAmountToUse.add(orderValue));
        } else {
            processRecalculatingPrePaymentInvoice(invoice, orderValue);
        }
    }

    private void processRecalculatingManyInvoices(List<Invoice> invoices, BigDecimal orderValue) {
        if (checkIfExistEqualizedInvoice(invoices)) {
            for (Invoice invoice : invoices) {
                if (invoice.isToEqualizeNegativeInvoice()) {
                    processRecalculatingEqualizedInvoice(invoice, orderValue);
                } else {
                    processRecalculatingNegativeInvoice(invoice, orderValue);
                }
            }
        } else {
            for (Invoice invoice : invoices) {
                if (invoice.getAmountToUse().compareTo(BigDecimal.ZERO) < 0) {
                    BigDecimal previousAmountToUse = invoice.getAmountToUse();
                    invoiceDao.delete(invoice);
                    orderValue = orderValue.add(previousAmountToUse);
                } else {
                    orderValue = processRecalculatingPrePaymentInvoice(invoice, orderValue);
                }
            }
        }
    }

    private void processRecalculatingEqualizedInvoice(Invoice invoice, BigDecimal orderValue) {
        BigDecimal previousAmountToUse = invoice.getAmountToUse();
        BigDecimal previousValue = invoice.getValue();
        BigDecimal newAmountToUse = previousAmountToUse.subtract(orderValue);
        BigDecimal newValue = previousValue.subtract(orderValue);
        if (newValue.compareTo(BigDecimal.ZERO) == 0) {
            invoiceDao.delete(invoice);
        } else {
            invoice.setAmountToUse(newAmountToUse);
            invoice.setValue(newValue);
            invoiceDao.save(invoice);
        }
    }

    private void processRecalculatingNegativeInvoice(Invoice invoice, BigDecimal orderValue) {
        BigDecimal previousAmountToUse = invoice.getAmountToUse();
        BigDecimal previousValue = invoice.getValue();
        BigDecimal newAmountToUse = previousAmountToUse.add(orderValue);
        BigDecimal newValue = previousValue.add(orderValue);
        if (previousAmountToUse.add(orderValue).compareTo(BigDecimal.ZERO) > 0) {
            invoiceDao.delete(invoice);
            Invoice lastUsedInvoice = invoiceDao.getLastUsedSupplierInvoice(invoice.getSupplier().getId());
            lastUsedInvoice.setAmountToUse(newAmountToUse);
            lastUsedInvoice.setUsed(false);
            invoiceDao.save(lastUsedInvoice);
        } else {
            if (newValue.compareTo(BigDecimal.ZERO) == 0) {
                invoiceDao.delete(invoice);
            } else {
                invoice.setAmountToUse(newAmountToUse);
                invoice.setValue(newValue);
                invoiceDao.save(invoice);
            }
        }
    }

    private BigDecimal processRecalculatingPrePaymentInvoice(Invoice invoice, BigDecimal orderValue) {
        BigDecimal lackDifference = invoice.getValue().subtract(invoice.getAmountToUse());
        if (orderValue.compareTo(lackDifference) > 0) {
            invoice.setAmountToUse(lackDifference);
            orderValue = orderValue.subtract(lackDifference);
            invoice.setUsed(false);
        } else {
            BigDecimal previousAmountToUse = invoice.getAmountToUse();
            BigDecimal newAmountToUse = previousAmountToUse.add(orderValue);
            invoice.setAmountToUse(newAmountToUse);
            invoice.setUsed(false);
            invoiceDao.save(invoice);
        }
        return orderValue;
    }

    private boolean checkIfExistEqualizedInvoice(List<Invoice> invoices) {
        for (Invoice invoice : invoices) {
            if (invoice.isToEqualizeNegativeInvoice()) {
                return true;
            }
        }
        return false;
    }

    private boolean checkIfValuesAreEqual(BigDecimal negativeInvoiceValue, BigDecimal orderDetailsValue) {
        BigDecimal convertedInvoiceValue = negativeInvoiceValue.multiply(BigDecimal.valueOf(-1));

        return convertedInvoiceValue.compareTo(orderDetailsValue) == 0;
    }

    private boolean isOrderIsRelatedWithEqualizedInvoice(Invoice invoice) {
        return getBuyerEqualizedInvoice(invoice) != null;
    }

    private Invoice getBuyerEqualizedInvoice(Invoice invoice) {
        List<Payment> payments = paymentDao.findByBuyerInvoiceId(invoice.getId());
        Invoice resultInvoice = null;
        for (Payment payment : payments) {
            List<Payment> extendedPayments = paymentDao.findByOrderDetailsId(payment.getOrderDetails().getId());
            for (Payment nestedPayment : extendedPayments) {
                if (nestedPayment.getBuyerInvoice() != null && !nestedPayment.getBuyerInvoice().getInvoiceNumber().equals("Negatywna")) {
                    resultInvoice = nestedPayment.getBuyerInvoice();
                }
            }
        }
        return resultInvoice;
    }

    private void updateBuyerOrder(UpdateOrderDetailsRequest updateOrderDetailsRequest, OrderDetails orderDetails) {
        List<Invoice> invoices = new ArrayList<>();
        List<Payment> payments = paymentDao.findBuyerPayment(orderDetails.getId());

        for (Payment payment : payments) {
            Long invoiceId = payment.getBuyerInvoice().getId();
            Invoice invoice = invoiceDao.findById(invoiceId)
                    .orElseThrow(RuntimeException::new);
            invoices.add(invoice);
        }

        if (invoices.size() == 1) {
            Invoice oldInvoice = invoices.get(0);

            processUpdatingBuyerInvoice(orderDetails, updateOrderDetailsRequest, oldInvoice, true);
            processUpdatingBuyerOrderDetails(orderDetails, updateOrderDetailsRequest);

        } else {
            Invoice oldInvoice = invoices.get(invoices.size() - 1);
            processUpdatingBuyerInvoice(orderDetails, updateOrderDetailsRequest, oldInvoice, false);
            processUpdatingBuyerOrderDetails(orderDetails, updateOrderDetailsRequest);

        }
        orderCommentService.addEditComment(orderDetails, " ZAMÓWIENIE EDYTOWANO");
    }

    private OrderDetails updateSupplierOrder(UpdateOrderDetailsRequest updateOrderDetailsRequest, OrderDetails orderDetails) {
        List<Invoice> invoices = new ArrayList<>();
        List<Payment> payments = paymentDao.findSupplierPayment(orderDetails.getId());

        for (Payment payment : payments) {
            Long invoiceId = payment.getSupplierInvoice().getId();
            Invoice invoice = invoiceDao.findById(invoiceId)
                    .orElseThrow(RuntimeException::new);
            invoices.add(invoice);
        }

        if (invoices.size() == 1) {
            Invoice oldInvoice = invoices.get(0);

            processUpdatingSupplierInvoice(orderDetails, updateOrderDetailsRequest, oldInvoice, true);
            processUpdatingSupplierOrderDetails(orderDetails, updateOrderDetailsRequest);

        } else {
            Invoice oldInvoice = invoices.get(invoices.size() - 1);
            processUpdatingSupplierInvoice(orderDetails, updateOrderDetailsRequest, oldInvoice, false);
            processUpdatingSupplierOrderDetails(orderDetails, updateOrderDetailsRequest);

        }
        orderCommentService.addEditComment(orderDetails, " ZAMÓWIENIE EDYTOWANO");

        return orderDetails;
    }

    private void processUpdatingBuyerInvoice(OrderDetails orderDetails,
                                             UpdateOrderDetailsRequest updateOrderDetailsRequest, Invoice invoice, boolean isOneInvoice) {
        BigDecimal oldBuyerSum = orderDetails.getBuyerSum();

        BigDecimal newBuyerPrice = updateOrderDetailsRequest.getNewBuyerPrice();
        BigDecimal newBuyerSum = updateOrderDetailsRequest.getNewQuantity().multiply(newBuyerPrice);
        updateOrderDetailsRequest.setNewBuyerSum(newBuyerSum);

        BigDecimal difference = oldBuyerSum.subtract(newBuyerSum);

        BigDecimal oldAmountToUse = invoice.getAmountToUse();

        if (isOneInvoice) {
            if (invoice.getAmountToUse().compareTo(BigDecimal.ZERO) < 0) {
                invoice.setAmountToUse(oldAmountToUse.add(difference));
            } else {
                BigDecimal previousInvoiceAmountToUse = oldAmountToUse.add(oldBuyerSum);
                BigDecimal newInvoiceAmountToUse = previousInvoiceAmountToUse.subtract(newBuyerSum);
                invoice.setAmountToUse(newInvoiceAmountToUse);

            }
            if (invoice.isCreatedToOrder()) {
                invoice.setAmountToUse(BigDecimal.ZERO);
                invoice.setValue(newBuyerSum);
            }
        } else {
            BigDecimal previousInvoiceAmountToUse = oldAmountToUse.add(oldBuyerSum);
            BigDecimal newInvoiceAmountToUse = previousInvoiceAmountToUse.subtract(newBuyerSum);
            invoice.setAmountToUse(newInvoiceAmountToUse);
            invoice.setUsed(false);
        }
        invoiceDao.save(invoice);
    }

    private void processUpdatingBuyerOrderDetails(OrderDetails orderDetails, UpdateOrderDetailsRequest updateOrderDetailsRequest) {
        orderDetails.setQuantity(updateOrderDetailsRequest.getNewQuantity());
        orderDetails.setBuyerSum(updateOrderDetailsRequest.getNewBuyerSum());
    }

    private void processUpdatingSupplierInvoice(OrderDetails orderDetails,
                                                UpdateOrderDetailsRequest updateOrderDetailsRequest, Invoice invoice, boolean isOneInvoice) {

        BigDecimal oldSupplierSum = orderDetails.getSupplierSum();

        BigDecimal newSupplierPrice = updateOrderDetailsRequest.getNewSupplierPrice();
        BigDecimal newSupplierSum = updateOrderDetailsRequest.getNewQuantity().multiply(newSupplierPrice);
        updateOrderDetailsRequest.setNewSupplierSum(newSupplierSum);

        BigDecimal difference = oldSupplierSum.subtract(newSupplierSum);

        BigDecimal oldAmountToUse = invoice.getAmountToUse();

        if (isOneInvoice) {
            if (invoice.getAmountToUse().compareTo(BigDecimal.ZERO) < 0) {
                invoice.setAmountToUse(oldAmountToUse.add(difference));
            } else {
                if (oldAmountToUse.compareTo(BigDecimal.ZERO) == 0) {
                    //TODO co jesli wartosc roznicy bedzie wieksza od wartosci dostepnej na fakturze?
                    //TODO w sensiena fv zostalo 30zl a trzeba odjac 100?
                    // nalezalo by wygenerowac wtedy negatywna fv
                    Invoice currentlyUsingInvoice = invoiceDao.getSupplierCurrentlyUsingInvoice(invoice.getSupplier().getId());
                    BigDecimal currentAmountToUse = currentlyUsingInvoice.getAmountToUse();
                    BigDecimal newAmountToUse = currentAmountToUse.add(difference);
                    currentlyUsingInvoice.setAmountToUse(newAmountToUse);
                } else {
                    BigDecimal previousInvoiceAmountToUse = oldAmountToUse.add(oldSupplierSum);
                    BigDecimal newInvoiceAmountToUse = previousInvoiceAmountToUse.subtract(oldSupplierSum);
                    invoice.setAmountToUse(newInvoiceAmountToUse);
                }
            }
        } else {
            BigDecimal previousInvoiceAmountToUse = oldAmountToUse.add(oldSupplierSum);
            BigDecimal newInvoiceAmountToUse = previousInvoiceAmountToUse.subtract(oldSupplierSum);
            invoice.setAmountToUse(newInvoiceAmountToUse);
            invoice.setUsed(false);
        }
        invoiceDao.save(invoice);
    }

    private void processUpdatingSupplierOrderDetails(OrderDetails orderDetails, UpdateOrderDetailsRequest updateOrderDetailsRequest) {
        orderDetails.setQuantity(updateOrderDetailsRequest.getNewQuantity());
        orderDetails.setSupplierSum(updateOrderDetailsRequest.getNewSupplierSum());
    }

    private BigDecimal calculatePreviousPrice(OrderDetails orderDetails, BigDecimal merchantSum) {
        BigDecimal quantity = orderDetails.getQuantity();
        return merchantSum.divide(quantity, RoundingMode.HALF_EVEN);
    }
*/

}
