package tradesystemsimplified.cost;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import tradesystemsimplified.user.RoleSecurity;

import java.util.List;

//@Log4j2
@AllArgsConstructor
@RestController
@RequestMapping("/cost")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class CostController {

    private CostMapper costMapper;
    private CostService costService;
    private RoleSecurity roleSecurity;

    //private Logger logger = LogManager.getLogger(CostController.class);


    @PostMapping("/create")
    public CostDto create(@RequestBody CostDto costDto) {
        //logger.info("Dodanie kosztu: " + costDto);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        roleSecurity.checkUserRole(authentication);

        final Cost cost = costService.createCost(costDto);

        return costMapper.toDto(cost);
    }

    @GetMapping("/getMonthCosts")
    public List<CostDto> getMonthCosts(@RequestParam(value = "localDate", required = false)
                                           @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) String localDate) {
        int year = Integer.valueOf(localDate.substring(0, 4));
        int month = Integer.valueOf(localDate.substring(5, 7));

        List<Cost> costs = costService.getMonthCosts(month, year);

        return costMapper.toDto(costs);
    }

    @DeleteMapping("/deleteCost")
    public void delete(@RequestParam(value = "name") String name) {
        //logger.info("Usuwanie kosztu: " + name);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        roleSecurity.checkUserRole(authentication);

        costService.deleteCost(name);

    }

}