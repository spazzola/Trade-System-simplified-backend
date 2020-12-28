package tradesystemsimplified.product;

import lombok.*;
import tradesystemsimplified.price.Price;
import tradesystemsimplified.price.pricehistory.PriceHistory;

import java.util.List;
import javax.persistence.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    private String product;

    @OneToMany(mappedBy = "product")
    private List<Price> prices;

    @OneToMany(mappedBy = "product")
    private List<PriceHistory> priceHistories;

}