package tradesystemsimplified.orderdetails;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tradesystemsimplified.product.ProductDto;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetailsDto {

    private BigDecimal quantity;
    private BigDecimal buyerSum;
    private BigDecimal supplierSum;
    private BigDecimal typedSoldPrice;
    private BigDecimal typedBoughtPrice;
    private String transportNumber;
    private String invoiceNumber;
    private ProductDto product;
    private boolean createBuyerInvoice;

}