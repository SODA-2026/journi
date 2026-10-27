package com.soda2026.journi.passwordResetToken;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/password-reset-tokens")
public class PasswordResetTokenController {
    private final PasswordResetTokenService passwordResetTokenService;

    public PasswordResetTokenController(PasswordResetTokenService passwordResetTokenService) {
        this.passwordResetTokenService = passwordResetTokenService;
    }

    @PostMapping("/validate")
    public PasswordResetTokenValidationDTO validate(@RequestBody PasswordResetTokenDTO dto) {
        return new PasswordResetTokenValidationDTO(passwordResetTokenService.isValid(dto.token()));
    }
}
