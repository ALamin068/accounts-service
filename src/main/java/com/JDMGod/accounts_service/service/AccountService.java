package com.JDMGod.accounts_service.service;

import com.JDMGod.accounts_service.api.dto.AccountCreateRequest;
import com.JDMGod.accounts_service.exception.DuplicateEmailException;
import com.JDMGod.accounts_service.exception.EntityNotFoundException;
import com.JDMGod.accounts_service.model.Account;
import com.JDMGod.accounts_service.repo.AccountRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    public AccountService(AccountRepository accountRepository, PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    public Optional<Account> getAccountById(Long id) {
        return accountRepository.findById(id);
    }

    public Optional<Account> getAccountByEmail(String email) {
        return accountRepository.findByEmail(email);
    }

    public Account createAccount(AccountCreateRequest request) {
        if (accountRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new DuplicateEmailException("Email already exists: " + request.getEmail());
        }

        Account account = new Account();
        account.setEmail(request.getEmail());
        account.setFullName(request.getFullName());
        account.setPassword(passwordEncoder.encode(request.getPassword()));
        account.setRole(request.getRole());
        account.setCreatedAt(LocalDateTime.now());
        account.setUpdatedAt(LocalDateTime.now());

        return accountRepository.save(account);
    }

    public Account updateAccount(Long id, AccountCreateRequest request) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Account not found with id: " + id));

        account.setFullName(request.getFullName());
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            account.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        account.setUpdatedAt(LocalDateTime.now());

        return accountRepository.save(account);
    }

    public void deleteAccount(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Account not found with id: " + id));
        account.setStatus(Account.Status.DELETED);
        account.setUpdatedAt(LocalDateTime.now());
        accountRepository.save(account);
    }
}
