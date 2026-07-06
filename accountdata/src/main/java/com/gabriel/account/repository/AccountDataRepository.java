package com.gabriel.account.repository;

import com.gabriel.account.entity.AccountData;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface AccountDataRepository extends CrudRepository<AccountData, Integer> {
    // Spring Data JPA generates: SELECT * FROM account_data WHERE email = ? (PreparedStatement)
    Optional<AccountData> findByEmail(String email);

    // Returns only non-admin (customer) accounts for the admin panel list
    java.util.List<AccountData> findByIsAdminFalse();
}