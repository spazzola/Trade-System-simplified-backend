package tradesystemsimplified.orderdetails;

import lombok.Getter;
import lombok.Setter;
import tradesystemsimplified.order.Order;
import tradesystemsimplified.payment.Payment;
import tradesystemsimplified.product.Product;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "order_details")
public class OrderDetails {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_details_id")
    private Long id;

    private BigDecimal quantity;

    private BigDecimal buyerSum;

    private BigDecimal supplierSum;

    private String transportNumber;

    @Transient
    private BigDecimal typedSoldPrice;

    @Transient
    private BigDecimal typedBoughtPrice;

    private String invoiceNumber;

    private boolean createBuyerInvoice;

    @ManyToOne(cascade= CascadeType.PERSIST)
    @JoinColumn(name = "order_fk")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "product_fk")
    private Product product;

    @OneToMany(mappedBy = "orderDetails")
    private List<Payment> paymentsList;

}