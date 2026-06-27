package com.gabriel.account.controller;

import com.gabriel.account.model.AccountType;
import com.gabriel.account.service.AccountTypeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class AccountTypeController {
    Logger logger = LoggerFactory.getLogger(AccountTypeController.class);

    @Autowired
    private AccountTypeService accountTypeService;

    @GetMapping("/api/accounttype")
    public ResponseEntity<?> listAccountTypes() {
        HttpHeaders headers = new HttpHeaders();
        ResponseEntity<?> response;
        try {
            AccountType[] types = accountTypeService.getAccountTypes();
            response = ResponseEntity.ok().headers(headers).body(types);
        } catch (Exception ex) {
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
        return response;
    }

    @PutMapping("api/accounttype")
    public ResponseEntity<?> add(@RequestBody AccountType type) {
        logger.info("Input >> " + type.toString());
        ResponseEntity<?> response;
        try {
            AccountType newType = accountTypeService.create(type);
            logger.info("created account type >> " + newType.toString());
            response = ResponseEntity.ok(newType);
        } catch (Exception ex) {
            logger.error("Failed to create account type: {}", ex.getMessage(), ex);
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
        return response;
    }

    @PostMapping("api/accounttype")
    public ResponseEntity<?> update(@RequestBody AccountType type) {
        logger.info("Update Input >> " + type.toString());
        ResponseEntity<?> response;
        try {
            AccountType newType = accountTypeService.update(type);
            response = ResponseEntity.ok(newType);
        } catch (Exception ex) {
            logger.error("Failed to update account type: {}", ex.getMessage(), ex);
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
        return response;
    }

    @GetMapping("api/accounttype/{id}")
    public ResponseEntity<?> get(@PathVariable final Integer id) {
        logger.info("Input account type id >> " + Integer.toString(id));
        ResponseEntity<?> response;
        try {
            AccountType type = accountTypeService.getAccountType(id);
            response = ResponseEntity.ok(type);
        } catch (Exception ex) {
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
        return response;
    }

    @DeleteMapping("api/accounttype/{id}")
    public ResponseEntity<?> delete(@PathVariable final Integer id) {
        logger.info("Input >> " + Integer.toString(id));
        ResponseEntity<?> response;
        try {
            accountTypeService.delete(id);
            response = ResponseEntity.ok(null);
        } catch (Exception ex) {
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
        return response;
    }
}
