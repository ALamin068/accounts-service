package com.JDMGod.accounts_service.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AccountCreateRequest(
        @Email @NotBlank String email,
        @NotBlank String fullName
) {}
