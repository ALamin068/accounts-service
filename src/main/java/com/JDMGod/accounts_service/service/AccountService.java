package com.JDMGod.accounts_service.service;

import com.JDMGod.accounts_service.api.dto.AccountCreateRequest;
import com.JDMGod.accounts_service.model.Account;
import com.JDMGod.accounts_service.repo.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountService {
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




}
