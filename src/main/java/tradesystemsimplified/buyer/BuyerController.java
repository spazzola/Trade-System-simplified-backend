package tradesystemsimplified.buyer;


import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Log4j2
@RestController
@RequestMapping("/buyer")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class BuyerController {

    private BuyerService buyerService;
    private BuyerMapper buyerMapper;
    private PriceMapper priceMapper;
    private OrderService orderService;
    private OrderMapper orderMapper;
    private InvoiceService invoiceService;
    private InvoiceMapper invoiceMapper;
    private RoleSecurity roleSecurity;

    private Logger logger = LogManager.getLogger(BuyerController.class);


    public BuyerController(BuyerService buyerService, BuyerMapper buyerMapper,
                           PriceMapper priceMapper, OrderService orderService,
                           OrderMapper orderMapper, InvoiceService invoiceService,
                           InvoiceMapper invoiceMapper, RoleSecurity roleSecurity) {
        this.buyerService = buyerService;
        this.buyerMapper = buyerMapper;
        this.priceMapper = priceMapper;
        this.orderService = orderService;
        this.orderMapper = orderMapper;
        this.invoiceService = invoiceService;
        this.invoiceMapper = invoiceMapper;
        this.roleSecurity = roleSecurity;
    }

    @PostMapping("/create")
    public BuyerDto createBuyer(@RequestBody BuyerDto buyerDto) {
        logger.info("Dodawanie kupca: " + buyerDto);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        roleSecurity.checkUserRole(authentication);

        final Buyer buyer = buyerService.createBuyer(buyerDto);

        return buyerMapper.toDto(buyer);
    }

    @GetMapping("/getAll")
    public List<BuyerDto> getAll() {
        final List<Buyer> buyers = buyerService.getAll();

        return buyerMapper.toDto(buyers);
    }

    @GetMapping("/getAllWithBalances")
    public List<BuyerDto> getAllWithRefreshedBalances(){
        final List<Buyer> buyers = buyerService.calculateAndSetBalances();

        return buyerMapper.toDto(buyers);
    }

    @GetMapping("/getAllWithAverageEarnings")
    public List<BuyerDto> getAllWithAverageEarnings(@RequestParam("localDate")
                                                    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) String localDate) {
        int year = Integer.valueOf(localDate.substring(0, 4));
        int month = Integer.valueOf(localDate.substring(5, 7));

        final List<Buyer> buyers = buyerService.getAllWithAverageEarning(month, year);
        return buyerMapper.toDto(buyers);
    }

    @GetMapping("/getBuyerProducts")
    public List<PriceDto> getBuyerProducts(@RequestParam("id") String id) {
        List<Price> prices = buyerService.getBuyerProducts(Long.valueOf(id));

        return priceMapper.toDto(prices);
    }

    @GetMapping("/getBuyerMonthOrders")
    public List<OrderDto> getBuyerMonthOrders(@RequestParam("buyerId") String buyerId,
                                              @RequestParam("month") String month,
                                              @RequestParam("year") String year) {

        Long id = Long.valueOf(buyerId);
        int m = Integer.valueOf(month);
        int y = Integer.valueOf(year);

        List<Order> orders = orderService.getBuyerMonthOrders(id, m, y);
        return orderMapper.toDto(orders);
    }

    @PutMapping("/updateBuyerName")
    public BuyerDto updateBuyerName(@RequestParam("oldBuyerName") String oldBuyerName,
                                    @RequestParam("newBuyerName") String newBuyerName) {

        logger.info("Aktualizacja nazwy kupca z: " + oldBuyerName + " na: " + newBuyerName);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        roleSecurity.checkUserRole(authentication);

        Buyer buyer = buyerService.updateBuyerName(oldBuyerName, newBuyerName);
        return buyerMapper.toDto(buyer);
    }

    @GetMapping("/getBuyerMonthInvoices")
    public List<InvoiceDto> getBuyerMonthInvoices(@RequestParam("buyerId") String buyerId,
                                                  @RequestParam("month") String month,
                                                  @RequestParam("year") String year) {

        Long id = Long.valueOf(buyerId);
        int m = Integer.valueOf(month);
        int y = Integer.valueOf(year);

        List<Invoice> invoices = invoiceService.getBuyerMonthInvoices(id, m, y);
        return invoiceMapper.toDto(invoices);
    }

    @GetMapping("/getBuyersMonthInvoices")
    public List<InvoiceDto> getBuyersMonthInvoices(@RequestParam("month") String month,
                                                   @RequestParam("year") String year) {
        int m = Integer.valueOf(month);
        int y = Integer.valueOf(year);

        List<Invoice> invoices = invoiceService.getBuyersMonthInvoices(m, y);
        return invoiceMapper.toDto(invoices);
    }

    @GetMapping("/getBuyersMonthTakenQuantity")
    public List<BuyerDto> getSuppliersMonthTakenQuantity(@RequestParam("localDate")
                                                         @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) String localDate) {
        int year = Integer.valueOf(localDate.substring(0, 4));
        int month = Integer.valueOf(localDate.substring(5, 7));

        List<Buyer> buyers = buyerService.getBuyersMonthTakenQuantity(month, year);
        return buyerMapper.toDto(buyers);
    }

}

