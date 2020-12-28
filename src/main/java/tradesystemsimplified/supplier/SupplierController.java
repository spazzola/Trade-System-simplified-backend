package tradesystemsimplified.supplier;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import tradesystemsimplified.invoice.Invoice;
import tradesystemsimplified.invoice.InvoiceDto;
import tradesystemsimplified.invoice.InvoiceMapper;
import tradesystemsimplified.invoice.InvoiceService;
import tradesystemsimplified.order.Order;
import tradesystemsimplified.order.OrderDto;
import tradesystemsimplified.order.OrderMapper;
import tradesystemsimplified.order.OrderService;
import tradesystemsimplified.price.Price;
import tradesystemsimplified.price.PriceDto;
import tradesystemsimplified.price.PriceMapper;
import tradesystemsimplified.user.RoleSecurity;

import java.util.List;

@AllArgsConstructor
@Log4j2
@RestController
@RequestMapping("/supplier")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class SupplierController {

    private SupplierService supplierService;
    private SupplierMapper supplierMapper;
    private PriceMapper priceMapper;
    private OrderService orderService;
    private OrderMapper orderMapper;
    private InvoiceService invoiceService;
    private InvoiceMapper invoiceMapper;
    private RoleSecurity roleSecurity;

    private Logger logger = LogManager.getLogger(SupplierController.class);


    @PostMapping("/create")
    public SupplierDto createBuyer(@RequestBody SupplierDto supplierDto) {
        logger.info("Dodawanie sprzedawcy: " + supplierDto);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        roleSecurity.checkUserRole(authentication);

        final Supplier supplier = supplierService.createSupplier(supplierDto);

        return supplierMapper.toDto(supplier);
    }

    @GetMapping("/getAll")
    public List<SupplierDto> getAll() {
        final List<Supplier> suppliers = supplierService.getAll();

        return supplierMapper.toDto(suppliers);
    }

    @GetMapping("/getAllWithBalances")
    public List<SupplierDto> getAllWithBalances(){
        final List<Supplier> suppliers = supplierService.getBalances();
        return supplierMapper.toDto(suppliers);
    }

    @GetMapping("/getSupplierProducts")
    public List<PriceDto> getSupplierProducts(@RequestParam("id") String id) {
        final List<Price> prices = supplierService.getSupplierProducts(Long.valueOf(id));

        return priceMapper.toDto(prices);
    }

    @GetMapping("/getSupplierMonthOrders")
    public List<OrderDto> getSupplierMonthOrders(@RequestParam("supplierId") String supplierId,
                                                 @RequestParam("month") String month,
                                                 @RequestParam("year") String year) {

        Long id = Long.valueOf(supplierId);
        int m = Integer.valueOf(month);
        int y = Integer.valueOf(year);

        List<Order> orders = orderService.getSupplierMonthOrders(id, m, y);
        return orderMapper.toDto(orders);
    }

    @PutMapping("/updateSupplierName")
    public SupplierDto updateSupplierName(@RequestParam("oldSupplierName") String oldSupplierName,
                                          @RequestParam("newSupplierName") String newSupplierName) {

        logger.info("Aktualizacja nazwy sprzedawcy z: " + oldSupplierName + " na: " + newSupplierName);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        roleSecurity.checkUserRole(authentication);

        Supplier supplier = supplierService.updateSupplierName(oldSupplierName, newSupplierName);
        return supplierMapper.toDto(supplier);
    }

    @GetMapping("/getSupplierMonthInvoices")
    public List<InvoiceDto> getSupplierMonthInvoices(@RequestParam("supplierId") String supplierId,
                                                     @RequestParam("month") String month,
                                                     @RequestParam("year") String year) {

        Long id = Long.valueOf(supplierId);
        int m = Integer.valueOf(month);
        int y = Integer.valueOf(year);

        List<Invoice> invoices = invoiceService.getSupplierMonthInvoices(id, m, y);
        return invoiceMapper.toDto(invoices);
    }

    @GetMapping("/getSuppliersMonthInvoices")
    public List<InvoiceDto> getSuppliersMonthInvoices(@RequestParam("month") String month,
                                                      @RequestParam("year") String year) {
        int m = Integer.valueOf(month);
        int y = Integer.valueOf(year);

        List<Invoice> invoices = invoiceService.getSuppliersMonthInvoices(m, y);
        return invoiceMapper.toDto(invoices);
    }

    @GetMapping("/getSuppliersMonthTakenQuantity")
    public List<SupplierDto> getSuppliersMonthTakenQuantity(@RequestParam("localDate")
                                                            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) String localDate) {
        int year = Integer.valueOf(localDate.substring(0, 4));
        int month = Integer.valueOf(localDate.substring(5, 7));

        List<Supplier> suppliers = supplierService.getSuppliersMonthTakenQuantity(month, year);

        return supplierMapper.toDto(suppliers);
    }

}