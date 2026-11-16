package com.soda2026.journi.invitation.Dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record InvitationRequestDto(
        @NotNull @Positive Long tripId,
        @NotBlank @Email String email
) {}
