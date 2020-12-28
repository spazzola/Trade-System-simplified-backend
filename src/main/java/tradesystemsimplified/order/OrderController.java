package tradesystemsimplified.order;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import tradesystemsimplified.orderdetails.OrderDetailsMapper;
import tradesystemsimplified.orderdetails.OrderDetailsService;
import tradesystemsimplified.orderdetails.UpdateOrderDetailsService;
import tradesystemsimplified.user.RoleSecurity;

import java.util.List;

@AllArgsConstructor
@Log4j2
@RestController
@RequestMapping("/order")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class OrderController {

    private OrderService orderService;
    private OrderMapper orderMapper;
    private OrderDao orderDao;
    private OrderDetailsService orderDetailsService;
    private OrderDetailsMapper orderDetailsMapper;
    private UpdateOrderDetailsService updateOrderDetailsService;
    private RoleSecurity roleSecurity;

    private Logger logger = LogManager.getLogger(OrderController.class);


    @PostMapping("/create")
    public OrderDto createOrder(@RequestBody CreateOrderRequest createOrderRequest) {
        logger.info("Dodawanie zamówienia: " + createOrderRequest);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        roleSecurity.checkUserRole(authentication);

        final Order order = orderService.createOrder(createOrderRequest);

        return orderMapper.toDto(order);
    }

    @GetMapping("/getAll")
    public List<OrderDto> getOrders() {
        final List<Order> ordersDto = orderService.getAllOrders();

        return orderMapper.toDto(ordersDto);
    }

    @GetMapping("/getMonthOrders")
    public List<OrderDto> getMonthOrders(@RequestParam(value = "year") String year,
                                         @RequestParam(value = "month") String month) {
        int y = Integer.valueOf(year);
        int m = Integer.valueOf(month);
        final List<Order> ordersDto = orderDao.getMonthOrders2(m, y);

        return orderMapper.toDto(ordersDto);
    }

    @GetMapping("/getOrderById")
    public OrderDto getOrderById(@RequestParam(value = "orderId") String orderId) {
        Long id = Long.valueOf(orderId);
        Order order = orderService.getOrderById(id);
        return orderMapper.toDto(order);
    }

    @PutMapping("/updateOrder")
    public void updateOrder(@RequestBody UpdateOrderDetailsRequest updateOrderDetailsRequest) {
        logger.info("Aktualizowanie zamówienia: " + updateOrderDetailsRequest);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        roleSecurity.checkUserRole(authentication);

        updateOrderDetailsService.updateOrder(updateOrderDetailsRequest);
    }

    @DeleteMapping("/deleteOrder")
    public void deleteOrder(@RequestParam(value = "id") Long id) {
        logger.info("Usuwanie zamówienia: id=" + id);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        roleSecurity.checkUserRole(authentication);

       updateOrderDetailsService.deleteOrder(id);
    }

}
