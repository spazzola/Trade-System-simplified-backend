package tradesystemsimplified.orderdetails;

import org.springframework.stereotype.Component;
import tradesystemsimplified.product.ProductDto;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderDetailsMapper {

    public OrderDetailsDto toDto(OrderDetails orderDetails) {
        ProductDto productDto = ProductDto.builder()
                .id(orderDetails.getProduct().getId())
                .product(orderDetails.getProduct().getProduct())
                .build();

        return OrderDetailsDto.builder()
                .quantity(orderDetails.getQuantity())
                .buyerSum(orderDetails.getBuyerSum())
                .supplierSum(orderDetails.getSupplierSum())
                .product(productDto)
                .transportNumber(orderDetails.getTransportNumber())
                .build();
    }


    public List<OrderDetailsDto> toDto(List<OrderDetails> orderDetails) {
        return orderDetails.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }


}