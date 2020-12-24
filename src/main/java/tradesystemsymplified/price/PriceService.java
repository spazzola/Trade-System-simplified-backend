package tradesystemsymplified.price;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tradesystemsymplified.buyer.Buyer;
import tradesystemsymplified.buyer.BuyerDto;
import tradesystemsymplified.product.Product;
import tradesystemsymplified.product.ProductDao;
import tradesystemsymplified.supplier.Supplier;
import tradesystemsymplified.supplier.SupplierDto;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class PriceService {

    private PriceDao priceDao;
    private ProductDao productDao;


    public PriceService(PriceDao priceDao, ProductDao productDao) {
        this.priceDao = priceDao;
        this.productDao = productDao;
    }


    @Transactional
    public Price createBuyerPrice(PriceDto priceDto) {
        BuyerDto buyerDto = priceDto.getBuyer();
        Price price;

        if (!validateBuyerAddedPrice(priceDto)) {
            throw new RuntimeException("Nie można stworzyć ceny dla kupca");
        } else {

            Buyer buyer = Buyer.builder()
                    .id(buyerDto.getId())
                    .build();

            Optional<Product> productOptional = productDao.findById(priceDto.getProduct().getId());
            Product product = null;

            if (productOptional.isPresent()) {
                product = productOptional.get();
            }

            price = Price.builder()
                    .price(priceDto.getPrice())
                    .product(product)
                    .buyer(buyer)
                    .build();
        }
        return priceDao.save(price);
    }

    @Transactional
    public Price createSupplierPrice(PriceDto priceDto) {
        SupplierDto supplierDto = priceDto.getSupplier();
        Price price;

        if (!validateSupplierAddedPrice(priceDto)) {
            throw new RuntimeException("Nie można stworzyć ceny dla dostawcy.");
        } else {

            Supplier supplier = Supplier.builder()
                    .id(supplierDto.getId())
                    .build();

            Optional<Product> product = productDao.findById(priceDto.getProduct().getId());

            price = Price.builder()
                    .price(priceDto.getPrice())
                    .product(product.get())
                    .supplier(supplier)
                    .build();
        }
        return priceDao.save(price);
    }

    @Transactional
    public void editBuyerPrice(Long buyerId, Long productId, BigDecimal newValue) {
        priceDao.updateBuyerPrice(buyerId, productId, newValue);
    }

    @Transactional
    public void editSupplierPrice(Long supplierId, Long productId, BigDecimal newValue) {
        priceDao.updateSupplierPrice(supplierId, productId, newValue);
    }

    private boolean validateBuyerAddedPrice(PriceDto priceDto) {
        if (priceDto.getBuyer() == null || priceDto.getBuyer().getId() <= 0) {
            return false;
        } else {
            Long buyerId = priceDto.getBuyer().getId();
            Long productId = priceDto.getProduct().getId();
            return priceDao.getPriceForBuyerAndProduct(buyerId, productId) == null;
        }
    }

    private boolean validateSupplierAddedPrice(PriceDto priceDto) {
        if (priceDto.getSupplier() == null || priceDto.getSupplier().getId() <= 0) {
            return false;
        } else {
            Long supplierId = priceDto.getSupplier().getId();
            Long productId = priceDto.getProduct().getId();

            return priceDao.getPriceForSupplierAndProduct(supplierId, productId) == null;
        }
    }

}
