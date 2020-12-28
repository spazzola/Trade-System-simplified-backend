package tradesystemsimplified.order;

import lombok.Data;
import tradesystemsimplified.buyer.Buyer;
import tradesystemsimplified.supplier.Supplier;

import java.math.BigDecimal;

@Data
public class UpdateOrderDetailsRequest {

    private Long id;
    private Buyer newBuyer;
    private Supplier newSupplier;
    private String oldTransportNumber;
    private String newTransportNumber;
    private BigDecimal newQuantity;
    private BigDecimal newBuyerPrice;
    private BigDecimal newBuyerSum;
    private BigDecimal newSupplierPrice;
    private BigDecimal newSupplierSum;

}