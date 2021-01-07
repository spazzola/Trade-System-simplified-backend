package tradesystemsimplified.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportDto {

    private BigDecimal soldValue;
    private BigDecimal boughtValue;
    private BigDecimal soldQuantity;
    private BigDecimal averageEarningsPerM3;
    private BigDecimal income;
    private BigDecimal sumCosts;
    private BigDecimal buyersNotPaidInvoices;
    private String type;

}