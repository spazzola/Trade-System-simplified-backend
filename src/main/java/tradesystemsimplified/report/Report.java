package tradesystemsimplified.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "reports")
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reportId;

    private BigDecimal soldValue;

    private BigDecimal boughtValue;

    private BigDecimal soldQuantity;

    private BigDecimal averageEarningsPerM3;

    private BigDecimal income;

    private BigDecimal buyersNotPaidInvoices;

    private BigDecimal sumCosts;

    private String type;


    @Override
    public String toString() {
        return "Report{" +
                "\nreportId=" + reportId +
                "\nsoldValue=" + soldValue +
                "\nsoldQuantity=" + soldQuantity +
                "\naverageEarningsPerM3=" + averageEarningsPerM3 +
                "\nincome=" + income +
                "\ntype='" + type + '\'' +
                '}';
    }
}