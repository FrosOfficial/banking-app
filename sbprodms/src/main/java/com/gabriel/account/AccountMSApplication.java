package com.gabriel.account;

import com.gabriel.account.entity.AccountTypeData;
import com.gabriel.account.repository.AccountTypeDataRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = "com.gabriel.account.entity")
@EnableJpaRepositories(basePackages = "com.gabriel.account.repository")
public class AccountMSApplication {
    public static void main(String[] args) {
        SpringApplication.run(AccountMSApplication.class, args);
    }

    @Bean
    public CommandLineRunner initData(AccountTypeDataRepository typeRepo) {
        return args -> {
            if (typeRepo.count() == 0) {
                AccountTypeData savings = new AccountTypeData();
                savings.setName("Savings");
                typeRepo.save(savings);

                AccountTypeData checking = new AccountTypeData();
                checking.setName("Checking");
                typeRepo.save(checking);

                System.out.println("Inserted default account types: Savings, Checking");
            }
        };
    }
}
