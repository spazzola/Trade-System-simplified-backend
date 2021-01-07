package tradesystemsimplified.report;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@AllArgsConstructor
@Service
public class ReportService {

    private final ReportDao reportDao;


    @Transactional
    public List<Report> getAllReports() {
        return reportDao.findAll();
    }

    public boolean checkIfReportExist(String type) {
        return reportDao.findByType(type) == null;
    }

}