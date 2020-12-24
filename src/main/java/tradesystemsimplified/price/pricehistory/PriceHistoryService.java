package tradesystemsimplified.price.pricehistory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tradesystemsimplified.buyer.Buyer;
import tradesystemsimplified.price.Price;
import tradesystemsimplified.product.Product;
import tradesystemsimplified.supplier.Supplier;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class PriceHistoryService {

    private PriceHistoryDao priceHistoryDao;

    public PriceHistoryService(PriceHistoryDao priceHistoryDao) {
        this.priceHistoryDao = priceHistoryDao;
    }

    @Transactional
    public PriceHistory createPriceHistory(Buyer buyer, Supplier supplier, Product product, BigDecimal price) {
        PriceHistory priceHistory = PriceHistory.builder()
                .buyer(buyer)
                .supplier(supplier)
                .product(product)
                .price(price)
                .build();

        if (validatePriceHistory(priceHistory)) {
            return priceHistoryDao.save(priceHistory);
        }
        return null;
    }

    @Transactional
    public List<Price> getBuyerPriceHistory(Long buyerId) {
        List<PriceHistory> pricesHistory = priceHistoryDao.findByBuyerId(buyerId);
        return changeTypeToPrice(pricesHistory);
    }

    private boolean validatePriceHistory(PriceHistory priceHistory) {
        List<PriceHistory> priceHistories = priceHistoryDao.findByBuyerId(priceHistory.getBuyer().getId());

        for (PriceHistory priceHistoryDB : priceHistories) {
            if (priceHistory.getSupplier().equals(priceHistoryDB.getSupplier()) && priceHistory.getPrice().equals(priceHistoryDB.getPrice())) {
                return false;
            }
        }
        return true;
    }

    private List<Price> changeTypeToPrice(List<PriceHistory> pricesHistory) {
        List<Price> prices = new ArrayList<>();

        for (PriceHistory priceHistory : pricesHistory) {
            prices.add(Price.builder()
                    .buyer(priceHistory.getBuyer())
                    .supplier(priceHistory.getSupplier())
                    .price(priceHistory.getPrice())
                    .product(priceHistory.getProduct())
                    .build());
        }
        return prices;
    }

}
