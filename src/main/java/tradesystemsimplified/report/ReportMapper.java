package tradesystemsimplified.report;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ReportMapper {


    public ReportDto toDto(Report report) {
        return ReportDto.builder()
                .soldValue(report.getSoldValue())
                .boughtValue(report.getBoughtValue())
                .soldQuantity(report.getSoldQuantity())
                .averageEarningsPerM3(report.getAverageEarningsPerM3())
                .income(report.getIncome())
                .sumCosts(report.getSumCosts())
                .buyersNotPaidInvoices(report.getBuyersNotPaidInvoices())
                .type(report.getType())
                .build();
    }

    public List<ReportDto> toDto(List<Report> invoices) {
        return invoices.stream()
                .map(this::toDto)
                .collect(Collectors.toList());

    }

}