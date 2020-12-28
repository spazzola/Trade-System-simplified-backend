package tradesystemsimplified.order;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tradesystemsimplified.buyer.Buyer;
import tradesystemsimplified.buyer.BuyerDao;
import tradesystemsimplified.orderdetails.OrderDetails;
import tradesystemsimplified.orderdetails.OrderDetailsDto;
import tradesystemsimplified.orderdetails.OrderDetailsService;
import tradesystemsimplified.product.Product;
import tradesystemsimplified.product.ProductDao;
import tradesystemsimplified.supplier.Supplier;
import tradesystemsimplified.supplier.SupplierDao;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

@AllArgsConstructor
@Service
public class OrderService {

    private BuyerDao buyerDao;
    private SupplierDao supplierDao;
    private ProductDao productDao;
    private OrderDao orderDao;
    private OrderDetailsService orderDetailsService;


    @Transactional
    public List<Order> getAllOrders() {
        return orderDao.findAll();
    }

    @Transactional
    public Set<Order> getMonthOrders(int month, int year) {
        return orderDao.getMonthOrders(month, year);
    }

    @Transactional
    public Order getOrderById(Long id) {
        return orderDao.findById(id)
                .orElseThrow(RuntimeException::new);
    }

    @Transactional
    public Order createOrder(CreateOrderRequest createOrderRequest) {
        Buyer buyer = buyerDao.findById(createOrderRequest.getBuyerId())
                .orElseThrow(RuntimeException::new);

        Supplier supplier = supplierDao.findById(createOrderRequest.getSupplierId())
                .orElseThrow(RuntimeException::new);

        if (validateOrder(createOrderRequest)) {
            List<OrderDetailsDto> orderDetailsDtoList = createOrderRequest.getOrderDetails();

            Order order = Order.builder()
                    .date(createOrderRequest.getDate())
                    .buyer(buyer)
                    .supplier(supplier)
                    .build();

            List<OrderDetails> orderDetailsList = new ArrayList<>();

            for (OrderDetailsDto orderDetailsDto : orderDetailsDtoList) {

                if (validateOrderDetail(orderDetailsDto)) {
                    OrderDetails orderDetails = new OrderDetails();
                    Long productId = orderDetailsDto.getProduct().getId();
                    Product product = productDao.findById(productId)
                            .orElseThrow(NoSuchElementException::new);

                    orderDetails.setProduct(product);
                    orderDetails.setTypedSoldPrice(orderDetailsDto.getTypedSoldPrice());
                    orderDetails.setTypedBoughtPrice(orderDetailsDto.getTypedBoughtPrice());
                    orderDetails.setTransportNumber(orderDetailsDto.getTransportNumber());
                    orderDetails.setQuantity(orderDetailsDto.getQuantity());
                    orderDetails.setOrder(order);
                    orderDetails.setCreateBuyerInvoice(orderDetailsDto.isCreateBuyerInvoice());

                    if (orderDetailsDto.isCreateBuyerInvoice()) {
                        orderDetails.setInvoiceNumber(orderDetailsDto.getInvoiceNumber());
                    }
                    // TODO add functionality to adding comments from user
                    // String userComment = orderDetailsDto.getOrderComment().getUserComment();
                    // orderDetails.getOrderComment().setUserComment(userComment);


                    orderDetailsList.add(orderDetails);

                } else {
                    throw new RuntimeException("Can't create order detail");
                }
            }

            order.setOrderDetails(orderDetailsList);

            return calculateOrder(order);
        } else {
            throw new RuntimeException("Can't create order");
        }

    }

    @Transactional
    public List<Order> getSupplierMonthOrders(Long supplierId, int month, int year) {
       return orderDao.getSupplierMonthOrders(supplierId, month, year);
    }

    @Transactional
    public List<Order> getBuyerMonthOrders(Long buyerId, int month, int year) {
        return orderDao.getBuyerMonthOrders(buyerId, month, year);
    }

    @Transactional
    public Order calculateOrder(Order order) {
        List<OrderDetails> orderDetails = order.getOrderDetails();
        for (OrderDetails orderDetail : orderDetails) {
            orderDetailsService.calculateOrderDetail(orderDetail);
        }
        return order;
    }

    private boolean validateOrder(CreateOrderRequest order) {
        if (order.getDate() == null) {
            return false;
        }
        if (order.getOrderDetails() == null) {
            return false;
        }
        return true;
    }

    private boolean validateOrderDetail(OrderDetailsDto orderDetail) {
        if (orderDetail.getQuantity().doubleValue() <= 0) {
            return false;
        }
        if (orderDetail.getProduct() == null) {
            return false;
        }
        return true;
    }

}