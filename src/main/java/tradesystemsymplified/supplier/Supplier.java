package tradesystemsymplified.supplier;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "suppliers")
public class Supplier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "supplier_id")
    private Long id;

    private String name;

    private BigDecimal currentBalance;

    private BigDecimal currentlyTakenQuantity;

    @Transient
    private BigDecimal monthTakenQuantity;

    @OneToMany(mappedBy = "supplier", fetch = FetchType.EAGER)
    private List<Price> prices;

    @OneToMany(mappedBy = "supplier")
    private List<Invoice> invoices;

    @OneToMany(mappedBy = "supplier")
    private List<Order> orders;

    @OneToMany(mappedBy = "supplier")
    private List<PriceHistory> priceHistories;

}

