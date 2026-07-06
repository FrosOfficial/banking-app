package com.gabriel.account.serviceimpl;

import com.gabriel.account.entity.AccountData;
import com.gabriel.account.model.Account;
import com.gabriel.account.repository.AccountDataRepository;
import com.gabriel.account.service.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // -----------------------------------------------------------------------
    // Internal helper: map entity -> model
    // -----------------------------------------------------------------------
    private Account toModel(AccountData d) {
        Account a = new Account();
        a.setId(d.getId());
        a.setName(d.getName());
        a.setDescription(d.getDescription());
        a.setAccountTypeId(d.getAccountTypeId());
        a.setAccountTypeName(d.getAccountTypeName());
        a.setBalance(d.getBalance());
        a.setEmail(d.getEmail());
        a.setPassword(d.getPassword()); // hashed value — never the raw password
        a.setAdmin(d.isAdmin());
        return a;
    }

    // -----------------------------------------------------------------------
    // Secure login lookup — parameterized query + BCrypt verify
    // Called only by the /api/account/login endpoint.
    // -----------------------------------------------------------------------
    public Account findByEmailAndPassword(String email, String rawPassword) {
        // DB does the lookup with a PreparedStatement (WHERE email = ?)
        Optional<AccountData> opt = accountDataRepository.findByEmail(email);
        if (opt.isEmpty()) {
            return null; // email not found — do not reveal which field failed
        }
        AccountData data = opt.get();
        // BCrypt compare — never a plain string equality check
        if (passwordEncoder.matches(rawPassword, data.getPassword())) {
            return toModel(data);
        }
        return null;
    }

    // -----------------------------------------------------------------------
    // CRUD
    // -----------------------------------------------------------------------
    @Override
    public Account[] getAccounts() {
        // Only return customer accounts — exclude system admin accounts from the list
        List<AccountData> accountsData = accountDataRepository.findByIsAdminFalse();
        List<Account> accounts = new ArrayList<>();
        for (AccountData data : accountsData) {
            accounts.add(toModel(data));
        }
        return accounts.toArray(new Account[0]);
    }

    @Override
    public Account create(Account account) {
        logger.info("create: Input " + account.toString());
        AccountData accountData = new AccountData();
        accountData.setName(account.getName());
        accountData.setDescription(account.getDescription());
        accountData.setAccountTypeId(account.getAccountTypeId());
        accountData.setAccountTypeName(account.getAccountTypeName());
        accountData.setBalance(account.getBalance());
        accountData.setEmail(account.getEmail());
        accountData.setAdmin(account.isAdmin());

        // Always hash the plain-text password before persisting
        if (account.getPassword() != null && !account.getPassword().isEmpty()) {
            accountData.setPassword(passwordEncoder.encode(account.getPassword()));
        }

        accountData = accountDataRepository.save(accountData);
        logger.info("create: Saved id=" + accountData.getId());
        return toModel(accountData);
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
        accountData.setAdmin(account.isAdmin());

        // Only re-hash when the caller sends a new plain-text password.
        // BCrypt hashes always start with "$2" — skip encoding if already hashed.
        String incoming = account.getPassword();
        if (incoming != null && !incoming.isEmpty() && !incoming.startsWith("$2")) {
            accountData.setPassword(passwordEncoder.encode(incoming));
        } else {
            accountData.setPassword(incoming); // preserve existing hash unchanged
        }

        accountData = accountDataRepository.save(accountData);
        return toModel(accountData);
    }

    @Override
    public Account getAccount(Integer id) {
        logger.info("getAccount id=" + id);
        Optional<AccountData> optional = accountDataRepository.findById(id);
        if (optional.isPresent()) {
            return toModel(optional.get());
        }
        logger.info("getAccount: not found id=" + id);
        return null;
    }

    @Override
    public void delete(Integer id) {
        logger.info("delete id=" + id);
        Optional<AccountData> optional = accountDataRepository.findById(id);
        if (optional.isPresent()) {
            accountDataRepository.delete(optional.get());
            logger.info("delete: success id=" + id);
        } else {
            logger.info("delete: not found id=" + id);
        }
    }
}
