package tradesystemsymplified.buyer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BuyerDto {

    private Long id;
    private String name;
    private BigDecimal currentBalance;
    private BigDecimal averageProfitPerM3;
    private BigDecimal monthTakenQuantity;

}
