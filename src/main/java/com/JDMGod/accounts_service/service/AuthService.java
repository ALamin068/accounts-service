package com.JDMGod.accounts_service.service;

import com.JDMGod.accounts_service.api.dto.AccountCreateRequest;
import com.JDMGod.accounts_service.api.dto.AuthResponse;
import com.JDMGod.accounts_service.api.dto.LoginRequest;
import com.JDMGod.accounts_service.constants.ApplicationConstants;
import com.JDMGod.accounts_service.exception.EntityNotFoundException;
import com.JDMGod.accounts_service.exception.InvalidTokenException;
import com.JDMGod.accounts_service.model.Account;
import com.JDMGod.accounts_service.repo.AccountRepository;
import com.JDMGod.accounts_service.security.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    private final AccountRepository accountRepository;
    private final AccountService accountService;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public AuthService(AccountRepository accountRepository, AccountService accountService,
                      JwtUtil jwtUtil, AuthenticationManager authenticationManager) {
        this.accountRepository = accountRepository;
        this.accountService = accountService;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
    }

    public AuthResponse register(AccountCreateRequest request) {
        // Use AccountService to create account (eliminates code duplication)
        Account account = accountService.createAccount(request);
        String token = generateTokenWithClaims(account);
        return new AuthResponse(token, account.getEmail(), account.getFullName(), account.getRole().name());
    }

    public AuthResponse login(LoginRequest request) {
        // Authenticate user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Account account = (Account) userDetails;
        String token = generateTokenWithClaims(account);
        return new AuthResponse(token, account.getEmail(), account.getFullName(), account.getRole().name());
    }

    public AuthResponse refreshToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith(ApplicationConstants.Security.BEARER_PREFIX)) {
            throw new InvalidTokenException("Invalid token format");
        }

        String token = authHeader.substring(ApplicationConstants.Security.BEARER_PREFIX.length());
        String username = jwtUtil.extractUsername(token);
        
        Account account = accountRepository.findByEmail(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + username));

        if (jwtUtil.validateToken(token, account)) {
            String newToken = generateTokenWithClaims(account);
            return new AuthResponse(newToken, account.getEmail(), account.getFullName(), account.getRole().name());
        }

        throw new InvalidTokenException("Invalid token");
    }

    private String generateTokenWithClaims(Account account) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("role", account.getRole().name());
        extraClaims.put("userId", account.getId());
        return jwtUtil.generateToken(account, extraClaims);
    }
}
