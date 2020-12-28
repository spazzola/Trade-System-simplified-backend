package tradesystemsimplified.order;

import lombok.Data;
import tradesystemsimplified.orderdetails.OrderDetailsDto;

import java.time.LocalDate;
import java.util.List;

@Data
public class CreateOrderRequest {

    private LocalDate date;
    private Long buyerId;
    private Long supplierId;
    private List<OrderDetailsDto> orderDetails;

}