package com.gabriel.account.serviceimpl;

import com.gabriel.account.entity.AccountData;
import com.gabriel.account.model.Account;
import com.gabriel.account.repository.AccountDataRepository;
import com.gabriel.account.service.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Service
public class AccountServiceImpl implements AccountService {
    Logger logger = LoggerFactory.getLogger(AccountServiceImpl.class);

    @Autowired
    AccountDataRepository accountDataRepository;

    @Override
    public Account[] getAccounts() {
        List<AccountData> accountsData = new ArrayList<>();
        List<Account> accounts = new ArrayList<>();
        accountDataRepository.findAll().forEach(accountsData::add);
        Iterator<AccountData> it = accountsData.iterator();

        while (it.hasNext()) {
            Account account = new Account();
            AccountData accountData = it.next();
            account.setId(accountData.getId());
            account.setName(accountData.getName());
            account.setDescription(accountData.getDescription());
            account.setAccountTypeId(accountData.getAccountTypeId());
            account.setAccountTypeName(accountData.getAccountTypeName());
            account.setBalance(accountData.getBalance());
            account.setEmail(accountData.getEmail());
            account.setPassword(accountData.getPassword());
            accounts.add(account);
        }

        Account[] array = new Account[accounts.size()];
        for (int i = 0; i < accounts.size(); i++) {
            array[i] = accounts.get(i);
        }
        return array;
    }

    @Override
    public Account create(Account account) {
        logger.info("add: Input " + account.toString());
        AccountData accountData = new AccountData();
        accountData.setName(account.getName());
        accountData.setDescription(account.getDescription());
        accountData.setAccountTypeId(account.getAccountTypeId());
        accountData.setAccountTypeName(account.getAccountTypeName());
        accountData.setBalance(account.getBalance());
        accountData.setEmail(account.getEmail());
        accountData.setPassword(account.getPassword());

        accountData = accountDataRepository.save(accountData);
        logger.info("add: Saved " + accountData.toString());

        Account newAccount = new Account();
        newAccount.setId(accountData.getId());
        newAccount.setName(accountData.getName());
        newAccount.setDescription(accountData.getDescription());
        newAccount.setAccountTypeId(accountData.getAccountTypeId());
        newAccount.setAccountTypeName(accountData.getAccountTypeName());
        newAccount.setBalance(accountData.getBalance());
        newAccount.setEmail(accountData.getEmail());
        newAccount.setPassword(accountData.getPassword());
        return newAccount;
    }

    @Override
    public Account update(Account account) {
        AccountData accountData = new AccountData();
        accountData.setId(account.getId());
        accountData.setName(account.getName());
        accountData.setDescription(account.getDescription());
        accountData.setAccountTypeId(account.getAccountTypeId());
        accountData.setAccountTypeName(account.getAccountTypeName());
        accountData.setBalance(account.getBalance());
        accountData.setEmail(account.getEmail());
        accountData.setPassword(account.getPassword());

        accountData = accountDataRepository.save(accountData);

        Account newAccount = new Account();
        newAccount.setId(accountData.getId());
        newAccount.setName(accountData.getName());
        newAccount.setDescription(accountData.getDescription());
        newAccount.setAccountTypeId(accountData.getAccountTypeId());
        newAccount.setAccountTypeName(accountData.getAccountTypeName());
        newAccount.setBalance(accountData.getBalance());
        newAccount.setEmail(accountData.getEmail());
        newAccount.setPassword(accountData.getPassword());
        return newAccount;
    }

    @Override
    public Account getAccount(Integer id) {
        logger.info("Input id >> " + Integer.toString(id));
        Optional<AccountData> optional = accountDataRepository.findById(id);
        if (optional.isPresent()) {
            logger.info("Is present >> ");
            Account account = new Account();
            AccountData accountDatum = optional.get();
            account.setId(accountDatum.getId());
            account.setName(accountDatum.getName());
            account.setDescription(accountDatum.getDescription());
            account.setAccountTypeId(accountDatum.getAccountTypeId());
            account.setAccountTypeName(accountDatum.getAccountTypeName());
            account.setBalance(accountDatum.getBalance());
            account.setEmail(accountDatum.getEmail());
            account.setPassword(accountDatum.getPassword());
            return account;
        }
        logger.info("Failed >> unable to locate account");
        return null;
    }

    @Override
    public void delete(Integer id) {
        logger.info("Input >> " + Integer.toString(id));
        Optional<AccountData> optional = accountDataRepository.findById(id);
        if (optional.isPresent()) {
            AccountData accountDatum = optional.get();
            accountDataRepository.delete(optional.get());
            logger.info("Successfully deleted >> " + accountDatum.toString());
        } else {
            logger.info("Failed >> unable to locate account id: " + Integer.toString(id));
        }
    }
}
