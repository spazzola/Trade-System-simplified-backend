package tradesystemsimplified.product;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class ProductService {

    private ProductDao productDao;


    public ProductService(ProductDao productDao) {
        this.productDao = productDao;
    }


    @Transactional
    public Product createProduct(ProductDto productDto) {
        if (validateProduct(productDto)) {
            final Product product = Product.builder()
                    .product(productDto.getProduct())
                    .build();
            return productDao.save(product);
        }
        throw new RuntimeException("Nie można stworzyć produktu");
    }

    @Transactional
    public Product getProduct(String productName) {
        return productDao.findByProduct(productName);
    }

    @Transactional
    public List<Product> getAllProducts() {
        return productDao.findAll();
    }

    private boolean validateProduct(ProductDto productDto) {
        if (productDto == null) {
            return false;
        }
        if (productDto.getProduct().equals("")) {
            return false;
        }
        if (productDao.findByProduct(productDto.getProduct()) != null) {
            return false;
        }
        return true;
    }

}
