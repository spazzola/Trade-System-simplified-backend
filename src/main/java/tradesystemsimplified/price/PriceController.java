package tradesystemsimplified.price;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import tradesystemsimplified.price.pricehistory.PriceHistoryService;
import tradesystemsimplified.user.RoleSecurity;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@Log4j2
@RestController
@RequestMapping("/price")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class PriceController {

    private PriceService priceService;
    private PriceMapper priceMapper;
    private PriceDao priceDao;
    private PriceHistoryService priceHistoryService;
    private RoleSecurity roleSecurity;

    private Logger logger = LogManager.getLogger(PriceController.class);


    @PostMapping("/createBuyerPrice")
    public PriceDto createBuyerPrice(@RequestBody PriceDto priceDto) {
        logger.info("Dodawanie ceny dla kupca: " + priceDto);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        roleSecurity.checkUserRole(authentication);

        final Price price = priceService.createBuyerPrice(priceDto);

        return priceMapper.toDto(price);
    }

    @PostMapping("/createSupplierPrice")
    public PriceDto createSupplierPrice(@RequestBody PriceDto priceDto) {
        logger.info("Dodawanie ceny dla sprzedawcy: " + priceDto);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        roleSecurity.checkUserRole(authentication);

        final Price price = priceService.createSupplierPrice(priceDto);

        return priceMapper.toDto(price);
    }

    @PutMapping("/editBuyerPrice")
    public void editBuyerPrice(@RequestParam(value = "buyerId", required = false) String buyerId,
                               @RequestParam(value = "productId", required = false) String productId,
                               @RequestParam(value = "value", required = false) String value) {

        logger.info("Edytowanie ceny dla kupca. buyer_id=" + buyerId + " product_id=" + productId + " wartość=" + value);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        roleSecurity.checkUserRole(authentication);

        BigDecimal newValue = new BigDecimal(value);
        priceService.editBuyerPrice(Long.valueOf(buyerId), Long.valueOf(productId), newValue);
    }

    @PutMapping("/editSupplierPrice")
    public void editSupplierPrice(@RequestParam(value = "supplierId") String supplierId,
                                  @RequestParam(value = "productId") String productId,
                                  @RequestParam(value = "value") String value) {

        logger.info("Edytowanie ceny dla sprzedawcy. supplier_id=" + supplierId + " product_id=" + productId + " wartość=" + value);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        roleSecurity.checkUserRole(authentication);

        BigDecimal newValue = new BigDecimal(value);
        priceService.editSupplierPrice(Long.valueOf(supplierId), Long.valueOf(productId), newValue);
    }

    @GetMapping("/getBuyerPriceHistory")
    public List<PriceDto> getBuyerPriceHistory(@RequestParam(value = "buyerId") String buyerId) {
        List<Price> pricesHistory =  priceHistoryService.getBuyerPriceHistory(Long.valueOf(buyerId));

        return priceMapper.toDto(pricesHistory);
    }

    @GetMapping("/getSupplierPrice")
    public BigDecimal getSupplierPrice(@RequestParam(value = "supplierId") String supplierId,
                                       @RequestParam(value = "productId") String productId) {

        return priceDao.getSupplierPrice(Long.valueOf(supplierId), Long.valueOf(productId));
    }

}