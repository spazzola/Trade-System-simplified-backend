package tradesystemsimplified.order;

import lombok.*;
import tradesystemsimplified.buyer.Buyer;
import tradesystemsimplified.orderdetails.OrderDetails;
import tradesystemsimplified.supplier.Supplier;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;

    private LocalDate date;

    @OneToMany(mappedBy = "order")
    private List<OrderDetails> orderDetails;

    @ManyToOne
    @JoinColumn(name = "buyer_fk")
    private Buyer buyer;

    @ManyToOne
    @JoinColumn(name = "supplier_fk")
    private Supplier supplier;

}