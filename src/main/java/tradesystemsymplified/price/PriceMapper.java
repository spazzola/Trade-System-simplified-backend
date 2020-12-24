package tradesystemsymplified.price;


import org.springframework.stereotype.Component;
import tradesystemsymplified.buyer.Buyer;
import tradesystemsymplified.buyer.BuyerDto;
import tradesystemsymplified.buyer.BuyerMapper;
import tradesystemsymplified.product.Product;
import tradesystemsymplified.product.ProductDto;
import tradesystemsymplified.product.ProductMapper;
import tradesystemsymplified.supplier.Supplier;
import tradesystemsymplified.supplier.SupplierDto;
import tradesystemsymplified.supplier.SupplierMapper;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PriceMapper {

    private BuyerMapper buyerMapper;
    private SupplierMapper supplierMapper;
    private ProductMapper productMapper;


    public PriceMapper(BuyerMapper buyerMapper, SupplierMapper supplierMapper, ProductMapper productMapper) {
        this.buyerMapper = buyerMapper;
        this.supplierMapper = supplierMapper;
        this.productMapper = productMapper;
    }


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
