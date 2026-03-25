package com.eventostec.api.domain.coupon;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CouponRequestDTO(
        @NotBlank(message = "code is required")
        String code,
        @NotNull(message = "discount is required")
        @Positive(message = "discount must be positive")
        Integer discount,
        @NotNull(message = "valid is required")
        Long valid
) {
}