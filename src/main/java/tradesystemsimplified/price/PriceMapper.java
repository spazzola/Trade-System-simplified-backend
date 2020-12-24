package tradesystemsimplified.price;


import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import tradesystemsimplified.buyer.Buyer;
import tradesystemsimplified.buyer.BuyerDto;
import tradesystemsimplified.buyer.BuyerMapper;
import tradesystemsimplified.product.Product;
import tradesystemsimplified.product.ProductDto;
import tradesystemsimplified.product.ProductMapper;
import tradesystemsimplified.supplier.Supplier;
import tradesystemsimplified.supplier.SupplierDto;
import tradesystemsimplified.supplier.SupplierMapper;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Component
public class PriceMapper {

    private BuyerMapper buyerMapper;
    private SupplierMapper supplierMapper;
    private ProductMapper productMapper;


    public PriceDto toDto(Price price) {
        final Buyer buyer = price.getBuyer();
        final Supplier supplier = price.getSupplier();
        final Product product = price.getProduct();

        BuyerDto buyerDto = null;
        if (buyer != null) {
            buyerDto = buyerMapper.toDto(buyer);
        }

        SupplierDto supplierDto = null;
        if (supplier != null) {
            supplierDto = supplierMapper.toDto(supplier);
        }

        final ProductDto productDto = productMapper.toDto(product);

        return PriceDto.builder()
                .price(price.getPrice())
                .product(productDto)
                .buyer(buyerDto)
                .supplier(supplierDto)
                .build();
    }

    public List<PriceDto> toDto(List<Price> invoices) {
        return invoices.stream()
                .map(this::toDto)
                .collect(Collectors.toList());

    }

}
