package tradesystemsimplified.price.pricehistory;

import lombok.*;
import tradesystemsimplified.buyer.Buyer;
import tradesystemsimplified.product.Product;
import tradesystemsimplified.supplier.Supplier;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "prices_history")
public class PriceHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "price_history_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "buyer_fk")
    private Buyer buyer;

    @ManyToOne
    @JoinColumn(name = "supplier_fk")
    private Supplier supplier;

    @ManyToOne
    @JoinColumn(name = "product_fk")
    private Product product;

    private BigDecimal price;

}
