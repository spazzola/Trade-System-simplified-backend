package tradesystemsimplified.report;

import lombok.AllArgsConstructor;
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
@RequestMapping("/report")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ReportController {

    //TODO change String params to LocalDate (at front)

    private ReportService reportService;
    private ReportYearService reportYearService;
    private ReportMonthService reportMonthService;
    private ReportMapper reportMapper;
    private RoleSecurity roleSecurity;

    //private Logger logger = LogManager.getLogger(ReportController.class);


    @PostMapping("/generateMonthReport")
    public ReportDto generateMonthReport(@RequestParam("localDate")
                                             @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) String localDate) {
        //logger.info("Generowanie miesiecznego raportu. Data otrzymana w requescie: " + localDate);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        roleSecurity.checkUserRole(authentication);

        int year = Integer.valueOf(localDate.substring(0, 4));
        int month = Integer.valueOf(localDate.substring(5, 7));
        Report report = reportMonthService.generateMonthReport(month, year);

        return reportMapper.toDto(report);
    }

    @PostMapping("/generateYearReport")
    public ReportDto generateYearReport(@RequestParam("localDate")
                                            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) String localDate) {

        //logger.info("Generowanie rocznego raportu. Data otrzymana w requescie: " + localDate);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        roleSecurity.checkUserRole(authentication);

        int year = Integer.valueOf(localDate.substring(0, 4));
        Report report = reportYearService.generateYearReport(year);

        return reportMapper.toDto(report);
    }

    @GetMapping("getAll")
    public List<ReportDto> getAllReports() {
        List<Report> reports = reportService.getAllReports();

        return reportMapper.toDto(reports);
    }

}