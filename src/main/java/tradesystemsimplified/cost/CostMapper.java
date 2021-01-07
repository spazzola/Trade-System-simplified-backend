package tradesystemsimplified.cost;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CostMapper {


    public CostDto toDto(Cost cost) {
        return CostDto.builder()
                .name(cost.getName())
                .value(cost.getValue())
                .date(cost.getDate())
                .build();
    }

    public List<CostDto> toDto(List<Cost> costs) {
        return costs.stream()
                .map(this::toDto)
                .collect(Collectors.toList());

    }

}