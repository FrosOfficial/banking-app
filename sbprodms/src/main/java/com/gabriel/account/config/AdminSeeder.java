package com.gabriel.account.config;

import com.gabriel.account.entity.AccountData;
import com.gabriel.account.repository.AccountDataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Runs once on backend startup.
 * Seeds the admin account (admin@bank.com) with a BCrypt-hashed password
 * if no admin row exists yet. Credentials are never hardcoded in login logic.
 */
@Component
public class AdminSeeder implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(AdminSeeder.class);

    @Autowired
    private AccountDataRepository accountDataRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public void run(String... args) {
        String adminEmail = "admin@bank.com";
        Optional<AccountData> existing = accountDataRepository.findByEmail(adminEmail);
        if (existing.isEmpty()) {
            logger.info("AdminSeeder: No admin found — seeding default admin account.");
            AccountData admin = new AccountData();
            admin.setName("Administrator");
            admin.setDescription("System Administrator");
            admin.setEmail(adminEmail);
            // Hash stored in DB — plain-text password never persisted
            admin.setPassword(passwordEncoder.encode("AdminPass123!"));
            admin.setAdmin(true);
            admin.setBalance(0);
            accountDataRepository.save(admin);
            logger.info("AdminSeeder: Admin account created successfully.");
        } else {
            logger.info("AdminSeeder: Admin account already exists — skipping seed.");
        }
    }
}
