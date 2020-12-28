package tradesystemsimplified.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tradesystemsimplified.buyer.BuyerDto;
import tradesystemsimplified.orderdetails.OrderDetailsDto;
import tradesystemsimplified.supplier.SupplierDto;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {

    private Long id;
    private LocalDate date;
    private BuyerDto buyer;
    private List<OrderDetailsDto> orderDetails;
    private SupplierDto supplier;

}