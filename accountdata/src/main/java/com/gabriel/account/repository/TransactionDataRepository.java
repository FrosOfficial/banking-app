package com.gabriel.account.repository;

import com.gabriel.account.entity.TransactionData;
import org.springframework.data.repository.CrudRepository;
import java.util.List;

public interface TransactionDataRepository extends CrudRepository<TransactionData, Integer> {
    List<TransactionData> findByAccountIdOrderByCreatedDesc(int accountId);
}
