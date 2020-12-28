package tradesystemsimplified.product;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import tradesystemsimplified.user.RoleSecurity;

import java.util.List;

@AllArgsConstructor
@Log4j2
@RestController
@RequestMapping("/product")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ProductController {

    private ProductService productService;
    private ProductMapper productMapper;
    private RoleSecurity roleSecurity;

    private Logger logger = LogManager.getLogger(ProductController.class);


    @PostMapping("/create")
    public ProductDto create(@RequestBody ProductDto productDto) {
        logger.info("Dodawanie produktu: " + productDto);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        roleSecurity.checkUserRole(authentication);

        final Product product = productService.createProduct(productDto);

        return productMapper.toDto(product);
    }

    @GetMapping("/getProduct")
    public ProductDto getProduct(@RequestParam(value = "productName") String productName) {
        Product product = productService.getProduct(productName);
        return productMapper.toDto(product);
    }

    @GetMapping("/getAll")
    public List<ProductDto> getAllProducts() {
        List<Product> products =  productService.getAllProducts();

        return productMapper.toDto(products);
    }

}