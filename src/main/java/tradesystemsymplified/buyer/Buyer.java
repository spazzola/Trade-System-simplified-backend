package tradesystemsymplified.buyer;

import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "buyers")
public class Buyer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "buyer_id")
    private Long id;

    private String name;

    private BigDecimal currentBalance;

    @Transient
    private BigDecimal averageProfitPerM3;

    @Transient
    private BigDecimal monthTakenQuantity;

    @OneToMany(mappedBy = "buyer")
    private List<Price> prices;

    @OneToMany(mappedBy = "buyer")
    private List<Invoice> invoices;

    @OneToMany(mappedBy = "buyer")
    private List<Order> orders;

    @OneToMany(mappedBy = "buyer")
    private List<PriceHistory> priceHistories;

    @Override
    public String toString() {
        return "Buyer{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", currentBalance=" + currentBalance +
                ", averageProfitPerM3=" + averageProfitPerM3 +
                ", prices=" + prices +
                ", invoices=" + invoices +
                ", orders=" + orders +
                ", priceHistories=" + priceHistories +
                '}';
    }

}

