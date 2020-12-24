package tradesystemsymplified.supplier;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class SupplierMapper {

    public SupplierDto toDto(Supplier supplier) {
        return SupplierDto.builder()
                .id(supplier.getId())
                .name(supplier.getName())
                .currentBalance(supplier.getCurrentBalance())
                .currentlyTakenQuantity(supplier.getCurrentlyTakenQuantity())
                .monthTakenQuantity(supplier.getMonthTakenQuantity())
                .build();
    }

    public List<SupplierDto> toDto(List<Supplier> suppliers) {
        return suppliers.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

}
