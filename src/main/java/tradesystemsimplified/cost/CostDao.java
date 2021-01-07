package tradesystemsimplified.cost;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CostDao extends JpaRepository<Cost, Long> {

    @Query(value = "SELECT * FROM costs " +
            "WHERE MONTH(costs.date) = ?1 AND YEAR(costs.date) = ?2",
            nativeQuery = true)
    List<Cost> getMonthCosts(int month, int year);

    @Query(value = "SELECT * FROM costs " +
            "WHERE YEAR(costs.date) = ?1",
            nativeQuery = true)
    List<Cost> getYearCosts(int year);

    void deleteByName(String name);

}