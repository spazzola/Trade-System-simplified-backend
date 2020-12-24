package tradesystemsimplified.price;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tradesystemsimplified.buyer.BuyerDto;
import tradesystemsimplified.product.ProductDto;
import tradesystemsimplified.supplier.SupplierDto;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PriceDto {


    private BigDecimal price;
    private ProductDto product;
    private BuyerDto buyer;
    private SupplierDto supplier;

}
