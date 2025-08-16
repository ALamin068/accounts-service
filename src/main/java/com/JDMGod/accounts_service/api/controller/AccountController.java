package com.JDMGod.accounts_service.api.controller;


import com.JDMGod.accounts_service.api.dto.AccountCreateRequest;
import com.JDMGod.accounts_service.model.Account;
import com.JDMGod.accounts_service.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {
    private final AccountService accountService;
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }


    @PostMapping("/{id}")
    public ResponseEntity<Account> createAccount(@RequestBody @Valid AccountCreateRequest accountCreateRequest) {
        return ResponseEntity.ok(accountService.create(accountCreateRequest));

    }

    @GetMapping
    public ResponseEntity<Account> get(@PathVariable Long id){
        return ResponseEntity.ok(accountService.get(id));
    }
}
