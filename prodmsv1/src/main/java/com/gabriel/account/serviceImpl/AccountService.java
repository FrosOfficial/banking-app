package com.gabriel.account.serviceImpl;

import com.gabriel.account.model.Account;
import com.gabriel.account.model.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AccountService {
    Logger logger = LoggerFactory.getLogger(AccountService.class);
    static AccountService service = null;

    @Value("${service.api.endpoint}")
    private String endpointUrl = "http://localhost:8080/api/account";
    RestTemplate restTemplate = null;

    public static AccountService getService() {
        if (service == null) {
            service = new AccountService();
        }
        return service;
    }

    public RestTemplate getRestTemplate() {
        if (restTemplate == null) {
            restTemplate = new RestTemplate();
            List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
            MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
            converter.setSupportedMediaTypes(Collections.singletonList(MediaType.APPLICATION_JSON));
            messageConverters.add(converter);
            restTemplate.setMessageConverters(messageConverters);
        }
        return restTemplate;
    }

    public Account getAccount(Integer id) {
        String url = endpointUrl + "/" + Integer.toString(id);
        logger.info("getAccount: " + url);

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<?> request = new HttpEntity<>(null, headers);
        final ResponseEntity<Account> response =
                getRestTemplate().exchange(url, HttpMethod.GET, request, Account.class);
        return response.getBody();
    }

    public Account[] getAccounts() {
        String url = endpointUrl;
        logger.info("getAccounts: " + url);

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<?> request = new HttpEntity<>(null, headers);
        final ResponseEntity<Account[]> response =
                getRestTemplate().exchange(url, HttpMethod.GET, request, Account[].class);
        return response.getBody();
    }

    public Account create(Account account) {
        String url = endpointUrl;
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<Account> request = new HttpEntity<>(account, headers);
        final ResponseEntity<Account> response =
                getRestTemplate().exchange(url, HttpMethod.PUT, request, Account.class);
        return response.getBody();
    }

    public Account update(Account account) {
        logger.info("update: " + account.toString());
        String url = endpointUrl;
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<Account> request = new HttpEntity<>(account, headers);
        final ResponseEntity<Account> response =
                getRestTemplate().exchange(url, HttpMethod.POST, request, Account.class);
        return response.getBody();
    }

    public void delete(Integer id) {
        logger.info("delete: " + Integer.toString(id));
        String url = endpointUrl + "/" + Integer.toString(id);
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<?> request = new HttpEntity<>(null, headers);
        getRestTemplate().exchange(url, HttpMethod.DELETE, request, Account.class);
    }

    public Account deposit(Integer id, double amount) {
        logger.info("deposit: id=" + id + ", amount=" + amount);
        String url = endpointUrl + "/" + Integer.toString(id) + "/deposit?amount=" + amount;
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<?> request = new HttpEntity<>(null, headers);
        final ResponseEntity<Account> response =
                getRestTemplate().exchange(url, HttpMethod.POST, request, Account.class);
        return response.getBody();
    }

    public Account withdraw(Integer id, double amount) {
        logger.info("withdraw: id=" + id + ", amount=" + amount);
        String url = endpointUrl + "/" + Integer.toString(id) + "/withdraw?amount=" + amount;
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<?> request = new HttpEntity<>(null, headers);
        final ResponseEntity<Account> response =
                getRestTemplate().exchange(url, HttpMethod.POST, request, Account.class);
        return response.getBody();
    }

    public Transaction[] getTransactions(int accountId) {
        String url = endpointUrl + "/" + Integer.toString(accountId) + "/transactions";
        logger.info("getTransactions: " + url);
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<?> request = new HttpEntity<>(null, headers);
        final ResponseEntity<Transaction[]> response =
                getRestTemplate().exchange(url, HttpMethod.GET, request, Transaction[].class);
        return response.getBody();
    }

    /**
     * Sends credentials to POST /api/account/login.
     * The server performs a parameterized DB lookup and BCrypt verification.
     * Returns the Account (with isAdmin flag) on success, or null on failure.
     * No full account list is ever downloaded to the client.
     */
    public Account login(String email, String password) {
        String url = endpointUrl + "/login";
        logger.info("login: " + url);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Use a Map so MappingJackson2HttpMessageConverter serializes it as
        // {"email":"...","password":"..."} — a raw String would be double-encoded.
        java.util.Map<String, String> body = new java.util.HashMap<>();
        body.put("email", email);
        body.put("password", password);

        HttpEntity<java.util.Map<String, String>> request = new HttpEntity<>(body, headers);
        try {
            final ResponseEntity<Account> response =
                    getRestTemplate().exchange(url, HttpMethod.POST, request, Account.class);
            return response.getBody();
        } catch (Exception ex) {
            logger.warn("login: " + ex.getMessage());
            return null;
        }
    }
}
