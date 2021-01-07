package tradesystemsimplified.cost;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@AllArgsConstructor
@Service
public class CostService {

    private CostDao costDao;


    @Transactional
    public Cost createCost(CostDto costDto) {
        if (validateCost(costDto)) {
            Cost cost = Cost.builder()
                    .name(costDto.getName())
                    .value(costDto.getValue())
                    .date(costDto.getDate())
                    .build();

            return costDao.save(cost);
        }
        throw new RuntimeException("Can't create cost");
    }

    @Transactional
    public void deleteCost(String name) {
        costDao.deleteByName(name);
    }

    @Transactional
    public List<Cost> getMonthCosts(int month, int year) {
        return costDao.getMonthCosts(month, year);
    }

    private boolean validateCost(CostDto cost) {
        if (cost.getName().equals("") || cost.getName() == null) {
            return false;
        }
        if (cost.getDate() == null) {
            return false;
        }

        return !(cost.getValue().doubleValue() <= 0);
    }

}