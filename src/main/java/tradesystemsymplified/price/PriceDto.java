package tradesystemsymplified.price;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tradesystemsymplified.buyer.BuyerDto;
import tradesystemsymplified.product.ProductDto;
import tradesystemsymplified.supplier.SupplierDto;

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
