package com.gabriel.account.controller;

import com.gabriel.account.entity.TransactionData;
import com.gabriel.account.model.Account;
import com.gabriel.account.model.Transaction;
import com.gabriel.account.repository.TransactionDataRepository;
import com.gabriel.account.service.AccountService;
import com.gabriel.account.serviceimpl.AccountServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class AccountController {
    Logger logger = LoggerFactory.getLogger(AccountController.class);

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountServiceImpl accountServiceImpl;

    @Autowired
    private TransactionDataRepository transactionDataRepository;

    @GetMapping("/api/account")
    public ResponseEntity<?> listAccounts() {
        HttpHeaders headers = new HttpHeaders();
        ResponseEntity<?> response;
        try {
            Account[] accounts = accountService.getAccounts();
            response = ResponseEntity.ok().headers(headers).body(accounts);
        } catch (Exception ex) {
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
        return response;
    }

    @PutMapping("api/account")
    public ResponseEntity<?> add(@RequestBody Account account) {
        logger.info("Input >> " + account.toString());
        ResponseEntity<?> response;
        try {
            Account newAccount = accountService.create(account);
            logger.info("created account >> " + newAccount.toString());
            response = ResponseEntity.ok(newAccount);
        } catch (Exception ex) {
            logger.error("Failed to create account: {}", ex.getMessage(), ex);
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
        return response;
    }

    @PostMapping("api/account")
    public ResponseEntity<?> update(@RequestBody Account account) {
        logger.info("Update Input >> " + account.toString());
        ResponseEntity<?> response;
        try {
            Account newAccount = accountService.update(account);
            response = ResponseEntity.ok(newAccount);
        } catch (Exception ex) {
            logger.error("Failed to update account: {}", ex.getMessage(), ex);
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
        return response;
    }

    @GetMapping("api/account/{id}")
    public ResponseEntity<?> get(@PathVariable final Integer id) {
        logger.info("Input account id >> " + Integer.toString(id));
        ResponseEntity<?> response;
        try {
            Account account = accountService.getAccount(id);
            response = ResponseEntity.ok(account);
        } catch (Exception ex) {
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
        return response;
    }

    @DeleteMapping("api/account/{id}")
    public ResponseEntity<?> delete(@PathVariable final Integer id) {
        logger.info("Input >> " + Integer.toString(id));
        ResponseEntity<?> response;
        try {
            accountService.delete(id);
            response = ResponseEntity.ok(null);
        } catch (Exception ex) {
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
        return response;
    }

    /**
     * POST /api/account/login
     * Body: { "email": "...", "password": "..." }
     *
     * Security: uses a parameterized DB lookup (findByEmail) + BCrypt verification.
     * Returns the matching Account (with isAdmin flag) on success, or HTTP 401.
     * The client never receives other users' data.
     */
    @PostMapping("api/account/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");
        logger.info("Login attempt for email: " + email);
        if (email == null || password == null) {
            return ResponseEntity.badRequest().body("Email and password are required");
        }
        try {
            Account account = accountServiceImpl.findByEmailAndPassword(email, password);
            if (account != null) {
                logger.info("Login success: " + email + " (isAdmin=" + account.isAdmin() + ")");
                return ResponseEntity.ok(account);
            } else {
                logger.warn("Login failed for: " + email);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
            }
        } catch (Exception ex) {
            logger.error("Login error: {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }

    @PostMapping("api/account/{id}/deposit")
    public ResponseEntity<?> deposit(@PathVariable final Integer id, @RequestParam final double amount) {
        logger.info("Deposit request for id " + id + " amount " + amount);
        try {
            Account account = accountService.getAccount(id);
            if (account == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account not found");
            }
            if (amount <= 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid deposit amount");
            }
            account.setBalance(account.getBalance() + amount);
            Account updatedAccount = accountService.update(account);

            // Log Transaction
            TransactionData tx = new TransactionData();
            tx.setAccountId(id);
            tx.setType("DEPOSIT");
            tx.setAmount(amount);
            transactionDataRepository.save(tx);

            return ResponseEntity.ok(updatedAccount);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }

    @PostMapping("api/account/{id}/withdraw")
    public ResponseEntity<?> withdraw(@PathVariable final Integer id, @RequestParam final double amount) {
        logger.info("Withdraw request for id " + id + " amount " + amount);
        try {
            Account account = accountService.getAccount(id);
            if (account == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account not found");
            }
            if (amount <= 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid withdraw amount");
            }
            if (account.getBalance() < amount) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Insufficient balance");
            }
            account.setBalance(account.getBalance() - amount);
            Account updatedAccount = accountService.update(account);

            // Log Transaction
            TransactionData tx = new TransactionData();
            tx.setAccountId(id);
            tx.setType("WITHDRAWAL");
            tx.setAmount(amount);
            transactionDataRepository.save(tx);

            return ResponseEntity.ok(updatedAccount);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }

    @GetMapping("api/account/{id}/transactions")
    public ResponseEntity<?> getTransactions(@PathVariable final Integer id) {
        logger.info("Transactions request for account id >> " + Integer.toString(id));
        try {
            List<TransactionData> txDataList = transactionDataRepository.findByAccountIdOrderByCreatedDesc(id);
            List<Transaction> txList = txDataList.stream().map(tx -> {
                Transaction t = new Transaction();
                t.setId(tx.getId());
                t.setAccountId(tx.getAccountId());
                t.setType(tx.getType());
                t.setAmount(tx.getAmount());
                t.setCreated(tx.getCreated());
                return t;
            }).collect(Collectors.toList());
            return ResponseEntity.ok(txList);
        } catch (Exception ex) {
            logger.error("Failed to retrieve transactions: {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }
}
