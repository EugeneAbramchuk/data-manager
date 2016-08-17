package net.datamanager.application;

import java.util.List;
import java.util.Objects;

import io.codearte.jfairy.producer.person.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    List<Customer> findByLastNameStartsWithIgnoreCase(String lastName);

    @Query("SELECT sex, count(sex) FROM Customer GROUP BY sex")
    List<Object[]> countBySex();

    @Query(value = "SELECT CASE " +
            "         WHEN Age < 15 THEN '< 15' " +
            "         WHEN Age < 25 THEN '15 - 24' " +
            "         WHEN Age < 35 THEN '25 - 34' " +
            "         WHEN Age < 45 THEN '35 - 44' " +
            "         WHEN Age < 55 THEN '45 - 54' " +
            "         WHEN Age < 65 THEN '55 - 64' " +
            "         ELSE '65+' " +
            "       END AS Age, " +
            "       COUNT(*) AS Count " +
            "FROM Customer " +
            "GROUP BY CASE " +
            "         WHEN Age < 15 THEN '< 15' " +
            "         WHEN Age < 25 THEN '15 - 24' " +
            "         WHEN Age < 35 THEN '25 - 34' " +
            "         WHEN Age < 45 THEN '35 - 44' " +
            "         WHEN Age < 55 THEN '45 - 54' " +
            "         WHEN Age < 65 THEN '55 - 64' " +
            "         ELSE '65+' " +
            "         END " +
            "ORDER BY Age ASC")
    List<Object[]> countByAgeGroup();
}