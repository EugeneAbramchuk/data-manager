package net.datamanager.application;

import io.codearte.jfairy.Fairy;
import io.codearte.jfairy.producer.person.Person;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {

    private static final Fairy FAIRY = Fairy.create();
    private static final int CUSTOMER_COUNT = 100;

    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }

    @Bean
    public static CommandLineRunner loadData(CustomerRepository repository) {
        return (args) -> {
            for (int i = 0; i < CUSTOMER_COUNT; i++) {
                Person person = FAIRY.person();
                repository.save(new Customer(person.firstName(), person.lastName(),
                        person.sex(), person.age()));
            }
        };
    }

}