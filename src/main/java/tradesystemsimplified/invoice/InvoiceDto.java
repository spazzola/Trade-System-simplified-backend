package tradesystemsimplified.invoice;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tradesystemsimplified.buyer.BuyerDto;
import tradesystemsimplified.supplier.SupplierDto;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceDto {

    private Long id;
    private String invoiceNumber;
    private LocalDate date;
    private BigDecimal value;
    private boolean isPaid;
    private String comment;
    private BuyerDto buyer;
    private SupplierDto supplier;

}
