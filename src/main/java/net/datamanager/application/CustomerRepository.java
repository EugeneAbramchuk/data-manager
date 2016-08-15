package net.datamanager.application;

import java.util.List;
import java.util.Objects;

import io.codearte.jfairy.producer.person.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    List<Customer> findByLastNameStartsWithIgnoreCase(String lastName);

//    @Query("SELECT c.sex as Sex, count(c.sex) as Count FROM customer c GROUP BY c.sex")
//    List<Object[]> countBySex();
}