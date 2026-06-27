package com.gabriel.account.service;

import com.gabriel.account.model.Account;

public interface AccountService {
    Account[] getAccounts() throws Exception;

    Account getAccount(Integer id) throws Exception;

    Account create(Account account) throws Exception;

    Account update(Account account) throws Exception;

    void delete(Integer id) throws Exception;
}
