package com.gabriel.account.repository;

import com.gabriel.account.entity.AccountData;
import org.springframework.data.repository.CrudRepository;

public interface AccountDataRepository extends CrudRepository<AccountData, Integer> {}