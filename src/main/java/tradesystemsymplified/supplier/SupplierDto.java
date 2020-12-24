package tradesystemsymplified.supplier;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupplierDto {

    private Long id;
    private String name;
    private BigDecimal currentBalance;
    private BigDecimal currentlyTakenQuantity;
    private BigDecimal monthTakenQuantity;

}
