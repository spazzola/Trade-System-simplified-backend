package tradesystemsimplified.order;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import tradesystemsimplified.buyer.Buyer;
import tradesystemsimplified.buyer.BuyerDto;
import tradesystemsimplified.buyer.BuyerMapper;
import tradesystemsimplified.orderdetails.OrderDetailsDto;
import tradesystemsimplified.orderdetails.OrderDetailsMapper;
import tradesystemsimplified.supplier.Supplier;
import tradesystemsimplified.supplier.SupplierDto;
import tradesystemsimplified.supplier.SupplierMapper;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Component
public class OrderMapper {

    private BuyerMapper buyerMapper;
    private SupplierMapper supplierMapper;
    private OrderDetailsMapper orderDetailsMapper;


    public OrderDto toDto(Order order) {
        final Buyer buyer = order.getBuyer();
        final Supplier supplier = order.getSupplier();
        final List<OrderDetailsDto> orderDetailsDto = orderDetailsMapper.toDto(order.getOrderDetails());

        final BuyerDto buyerDto = buyerMapper.toDto(buyer);
        final SupplierDto supplierDto = supplierMapper.toDto(supplier);

        return OrderDto.builder()
                .id(order.getId())
                .date(order.getDate())
                .supplier(supplierDto)
                .buyer(buyerDto)
                .orderDetails(orderDetailsDto)
                .build();
    }

    public List<OrderDto> toDto(List<Order> invoices) {
        return invoices.stream()
                .map(this::toDto)
                .collect(Collectors.toList());

    }

}