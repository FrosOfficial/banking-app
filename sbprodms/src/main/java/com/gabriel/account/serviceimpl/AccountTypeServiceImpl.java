package com.gabriel.account.serviceimpl;

import com.gabriel.account.entity.AccountTypeData;
import com.gabriel.account.model.AccountType;
import com.gabriel.account.repository.AccountTypeDataRepository;
import com.gabriel.account.service.AccountTypeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Service
public class AccountTypeServiceImpl implements AccountTypeService {
    Logger logger = LoggerFactory.getLogger(AccountTypeServiceImpl.class);

    @Autowired
    AccountTypeDataRepository accountTypeDataRepository;

    @Override
    public AccountType[] getAccountTypes() {
        List<AccountTypeData> typesData = new ArrayList<>();
        List<AccountType> types = new ArrayList<>();
        accountTypeDataRepository.findAll().forEach(typesData::add);
        Iterator<AccountTypeData> it = typesData.iterator();

        while (it.hasNext()) {
            AccountType type = new AccountType();
            AccountTypeData typeData = it.next();
            type.setId(typeData.getId());
            type.setName(typeData.getName());
            types.add(type);
        }

        AccountType[] array = new AccountType[types.size()];
        for (int i = 0; i < types.size(); i++) {
            array[i] = types.get(i);
        }
        return array;
    }

    @Override
    public AccountType create(AccountType type) {
        logger.info("add: Input " + type.toString());
        AccountTypeData typeData = new AccountTypeData();
        typeData.setName(type.getName());

        typeData = accountTypeDataRepository.save(typeData);
        logger.info("add: Saved " + typeData.toString());

        AccountType newType = new AccountType();
        newType.setId(typeData.getId());
        newType.setName(typeData.getName());
        return newType;
    }

    @Override
    public AccountType update(AccountType type) {
        AccountTypeData typeData = new AccountTypeData();
        typeData.setId(type.getId());
        typeData.setName(type.getName());

        typeData = accountTypeDataRepository.save(typeData);

        AccountType newType = new AccountType();
        newType.setId(typeData.getId());
        newType.setName(typeData.getName());
        return newType;
    }

    @Override
    public AccountType getAccountType(Integer id) {
        logger.info("Input id >> " + Integer.toString(id));
        Optional<AccountTypeData> optional = accountTypeDataRepository.findById(id);
        if (optional.isPresent()) {
            AccountType type = new AccountType();
            AccountTypeData typeData = optional.get();
            type.setId(typeData.getId());
            type.setName(typeData.getName());
            return type;
        }
        return null;
    }

    @Override
    public void delete(Integer id) {
        logger.info("Input >> " + Integer.toString(id));
        Optional<AccountTypeData> optional = accountTypeDataRepository.findById(id);
        if (optional.isPresent()) {
            accountTypeDataRepository.delete(optional.get());
        }
    }
}
