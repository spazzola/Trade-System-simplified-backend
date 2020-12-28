package tradesystemsimplified.invoice;

import lombok.*;
import tradesystemsimplified.buyer.Buyer;
import tradesystemsimplified.supplier.Supplier;


import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "invoices")
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "invoice_id")
    private Long id;

    private String invoiceNumber;
    private LocalDate date;
    private BigDecimal value;
    private boolean isPaid;
    private String comment;

    @ManyToOne
    @JoinColumn(name = "supplier_fk")
    private Supplier supplier;

    @ManyToOne
    @JoinColumn(name = "buyer_fk")
    private Buyer buyer;

}
