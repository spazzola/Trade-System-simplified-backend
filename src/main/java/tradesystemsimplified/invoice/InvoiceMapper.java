package tradesystemsimplified.invoice;

import org.springframework.stereotype.Component;
import tradesystemsimplified.buyer.Buyer;
import tradesystemsimplified.buyer.BuyerDto;
import tradesystemsimplified.buyer.BuyerMapper;
import tradesystemsimplified.supplier.Supplier;
import tradesystemsimplified.supplier.SupplierDto;
import tradesystemsimplified.supplier.SupplierMapper;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class InvoiceMapper {

    private BuyerMapper buyerMapper;
    private SupplierMapper supplierMapper;

    public InvoiceMapper(BuyerMapper buyerMapper, SupplierMapper supplierMapper) {
        this.buyerMapper = buyerMapper;
        this.supplierMapper = supplierMapper;
    }

    public InvoiceDto toDto(Invoice invoice) {
        final Buyer buyer = invoice.getBuyer();
        final Supplier supplier = invoice.getSupplier();

        final BuyerDto buyerDto = buyer != null ? buyerMapper.toDto(invoice.getBuyer()) : null;
        final SupplierDto supplierDto = supplier != null ? supplierMapper.toDto(invoice.getSupplier()) : null;

        return InvoiceDto.builder()
                .id(invoice.getId())
                .invoiceNumber(invoice.getInvoiceNumber())
                .date(invoice.getDate())
                .value(invoice.getValue())
                .isPaid(invoice.isPaid())
                .comment(invoice.getComment())
                .buyer(buyerDto)
                .supplier(supplierDto)
                .build();
    }

    public List<InvoiceDto> toDto(List<Invoice> invoices) {
        return invoices.stream()
                .map(this::toDto)
                .collect(Collectors.toList());

    }

}