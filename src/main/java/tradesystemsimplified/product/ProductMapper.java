package tradesystemsimplified.product;

import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductMapper {

    public ProductDto toDto(Product product) {
        return ProductDto.builder()
                .product(product.getProduct())
                .id(product.getId())
                .build();
    }

    public List<ProductDto> toDto(List<Product> invoices) {
        return invoices.stream()
                .map(this::toDto)
                .collect(Collectors.toList());

    }

}
