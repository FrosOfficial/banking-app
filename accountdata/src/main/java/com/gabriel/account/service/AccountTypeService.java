package com.gabriel.account.service;

import com.gabriel.account.model.AccountType;

public interface AccountTypeService {
    AccountType[] getAccountTypes() throws Exception;

    AccountType getAccountType(Integer id) throws Exception;

    AccountType create(AccountType accountType) throws Exception;

    AccountType update(AccountType accountType) throws Exception;

    void delete(Integer id) throws Exception;
}
