package tradesystemsimplified.price;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository("priceDao")
public interface PriceDao extends JpaRepository<Price, Long> {


    @Query(value = "SELECT price FROM prices " +
            "INNER JOIN products prod ON prices.product_fk = prod.id " +
            "WHERE prices.buyer_fk = :buyerId AND prices.product_fk = :id",
            nativeQuery = true)
    BigDecimal getBuyerPrice(@Param("buyerId") Long buyerId,
                             @Param("id") Long productId);


    @Query(value = "SELECT price FROM prices " +
            "INNER JOIN products prod ON prices.product_fk = prod.id " +
            "WHERE prices.supplier_fk = :supplierId AND prices.product_fk = :id",
            nativeQuery = true)
    BigDecimal getSupplierPrice(@Param("supplierId") Long supplierId,
                                @Param("id") Long productId);


    @Query(value = "SELECT * FROM prices " +
            "WHERE buyer_fk = ?1",
            nativeQuery = true)
    List<Price> getBuyerProducts(Long id);


    @Query(value = "SELECT * FROM prices " +
            "WHERE supplier_fk = ?1",
            nativeQuery = true)
    List<Price> getSupplierProducts(Long id);

    @Modifying
    @Query(value = "UPDATE prices " +
            "SET price = ?3 " +
            "WHERE buyer_fk = ?1 AND product_fk = ?2",
            nativeQuery = true)
    void updateBuyerPrice(Long buyerId, Long productId, BigDecimal newValue);


    @Modifying
    @Query(value = "UPDATE prices " +
            "SET price = ?3 " +
            "WHERE supplier_fk = ?1 AND product_fk = ?2",
            nativeQuery = true)
    void updateSupplierPrice(Long supplier_fk, Long productId, BigDecimal newValue);


    @Query(value = "SELECT * FROM prices " +
            "WHERE buyer_fk = ?1 AND product_fk = ?2",
            nativeQuery = true)
    Price getPriceForBuyerAndProduct(Long buyerId, Long productId);

    @Query(value = "SELECT * FROM prices " +
            "WHERE supplier_fk = ?1 AND product_fk = ?2",
            nativeQuery = true)
    Price getPriceForSupplierAndProduct(Long supplierId, Long productId);

}
