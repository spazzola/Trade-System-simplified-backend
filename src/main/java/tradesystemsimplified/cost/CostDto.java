package tradesystemsimplified.cost;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CostDto {

    private String name;
    private BigDecimal value;
    private LocalDate date;

}