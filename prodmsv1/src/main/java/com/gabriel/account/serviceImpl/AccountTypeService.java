package com.gabriel.account.serviceImpl;

import com.gabriel.account.model.AccountType;
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

public class AccountTypeService {
    Logger logger = LoggerFactory.getLogger(AccountTypeService.class);
    static AccountTypeService service = null;

    @Value("${service.api.endpoint}")
    private String endpointUrl = "http://localhost:8080/api/accounttype";
    RestTemplate restTemplate = null;

    public static AccountTypeService getService() {
        if (service == null) {
            service = new AccountTypeService();
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

    public AccountType getAccountType(Integer id) {
        String url = endpointUrl + "/" + Integer.toString(id);
        logger.info("getAccountType: " + url);

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<?> request = new HttpEntity<>(null, headers);
        final ResponseEntity<AccountType> response =
                getRestTemplate().exchange(url, HttpMethod.GET, request, AccountType.class);
        return response.getBody();
    }

    public AccountType[] getAccountTypes() {
        String url = endpointUrl;
        logger.info("getAccountTypes: " + url);

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<?> request = new HttpEntity<>(null, headers);
        final ResponseEntity<AccountType[]> response =
                getRestTemplate().exchange(url, HttpMethod.GET, request, AccountType[].class);
        return response.getBody();
    }

    public AccountType create(AccountType type) {
        String url = endpointUrl;
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<AccountType> request = new HttpEntity<>(type, headers);
        final ResponseEntity<AccountType> response =
                getRestTemplate().exchange(url, HttpMethod.PUT, request, AccountType.class);
        return response.getBody();
    }

    public AccountType update(AccountType type) {
        logger.info("update: " + type.toString());
        String url = endpointUrl;
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<AccountType> request = new HttpEntity<>(type, headers);
        final ResponseEntity<AccountType> response =
                getRestTemplate().exchange(url, HttpMethod.POST, request, AccountType.class);
        return response.getBody();
    }

    public void delete(Integer id) {
        logger.info("delete: " + Integer.toString(id));
        String url = endpointUrl + "/" + Integer.toString(id);
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<?> request = new HttpEntity<>(null, headers);
        getRestTemplate().exchange(url, HttpMethod.DELETE, request, AccountType.class);
    }
}
