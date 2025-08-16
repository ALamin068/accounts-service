package com.JDMGod.accounts_service.service;

import com.JDMGod.accounts_service.api.dto.AccountCreateRequest;
import com.JDMGod.accounts_service.model.Account;
import com.JDMGod.accounts_service.repo.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

@Service
public class AccountService {
    @Autowired
    private final AccountRepository repo;
    public AccountService(AccountRepository repo) {this.repo = repo;}


    @Transactional
    public Account create(AccountCreateRequest req){
      repo.findByEmail(req.email()).ifPresent(account -> {
          throw new IllegalArgumentException("Account already exists");
      });
      Account account = new Account();
      account.setEmail(req.email());
      account.setFullName(req.fullName());
      account.setStatus("ACTIVE");
      return repo.save(account);

    }

    @Transactional(readOnly = true)
    public Account get(Long id){
        return repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Account not found"));
    }

    @Transactional(readOnly = true)
    public Account getByEmail(String email) {
        return repo.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("Account not found"));
    }

    @Transactional(readOnly = true)
    public List<Account> list() {
        return repo.findAll();
    }


    //Delete by ID
    @Transactional
    public void delete(Long id){
        repo.deleteById(id);
    }

}
