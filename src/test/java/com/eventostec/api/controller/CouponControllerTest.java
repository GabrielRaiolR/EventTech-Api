package com.eventostec.api.controller;

import com.eventostec.api.domain.coupon.Coupon;
import com.eventostec.api.domain.service.CouponService;
import com.eventostec.api.exception.GlobalExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CouponController.class)
@Import(GlobalExceptionHandler.class)
class CouponControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CouponService couponService;

    @Test
    void addCouponToEvent_returnsOk() throws Exception {
        UUID eventId = UUID.randomUUID();
        UUID couponId = UUID.randomUUID();

        Coupon coupon = new Coupon();
        coupon.setId(couponId);
        coupon.setCode("PROMO10");
        coupon.setDiscount(10);
        coupon.setValid(new Date(1893456000000L));

        when(couponService.addCouponToEvent(eq(eventId), any())).thenReturn(coupon);

        String body = objectMapper.writeValueAsString(Map.of(
                "code", "PROMO10",
                "discount", 10,
                "valid", 1893456000000L
        ));

        mockMvc.perform(
                        post("/api/coupon/event/{eventId}", eventId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("PROMO10"))
                .andExpect(jsonPath("$.discount").value(10));
    }

    @Test
    void addCoupon_withInvalidBody_returns400() throws Exception {
        UUID eventId = UUID.randomUUID();
        String body = objectMapper.writeValueAsString(Map.of(
                "code", "",
                "discount", 10,
                "valid", 1893456000000L
        ));

        mockMvc.perform(
                        post("/api/coupon/event/{eventId}", eventId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.fieldErrors").isArray());
    }
}
