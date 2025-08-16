package com.JDMGod.accounts_service.api.controller;


import com.JDMGod.accounts_service.api.dto.AccountCreateRequest;
import com.JDMGod.accounts_service.model.Account;
import com.JDMGod.accounts_service.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {
    @Autowired
    private final AccountService accountService;
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }


    @PostMapping("/saveAccount")
    public ResponseEntity<Account> createAccount(@RequestBody @Valid AccountCreateRequest accountCreateRequest) {
        return ResponseEntity.ok(accountService.create(accountCreateRequest));

    }

    @GetMapping("/{id}")
    public ResponseEntity<Account> get(@PathVariable Long id){
        return ResponseEntity.ok(accountService.get(id));
    }


    @GetMapping(params = "email")
    public ResponseEntity<Account> getByEmail(@RequestBody String email){
        return ResponseEntity.ok(accountService.getByEmail(email));

    }


    @GetMapping
    public ResponseEntity<List<Account>> getAll(){
        return ResponseEntity.ok(accountService.list());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Account> delete(@PathVariable Long id){
        accountService.delete(id);
        return ResponseEntity.noContent().build();
    }


}
