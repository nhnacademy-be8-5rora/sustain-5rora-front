package store.aurora.coupon.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import store.aurora.coupon.dto.CouponState;
import store.aurora.coupon.dto.SaleType;
import store.aurora.coupon.dto.request.DiscountRuleDTO;
import store.aurora.coupon.dto.request.RequestCouponPolicyDTO;
import store.aurora.coupon.dto.request.RequestUserCouponDTO;
import store.aurora.coupon.dto.request.UpdateUserCouponDto;
import store.aurora.feign_client.coupon.CouponClient;

import java.time.LocalDate;
import java.util.List;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(AdminCouponController.class)  // AdminCouponController 테스트
class AdminCouponControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private CouponClient couponClient;

    @InjectMocks
    private AdminCouponController adminCouponController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void testGoAdminCoupon() throws Exception {
        // 쿠폰 생성 페이지로 이동하는 테스트
        mockMvc.perform(MockMvcRequestBuilders.get("/admin/coupon/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/coupon/coupon-create"));
    }

    @Test
    void testCouponPolicyCreate() throws Exception {
        // 쿠폰 정책 생성 요청
        RequestCouponPolicyDTO requestCouponPolicyDTO = RequestCouponPolicyDTO.builder()
                .policyName("Test Policy")
                .saleType(SaleType.PERCENT)
                .discountRuleDTO(new DiscountRuleDTO()) // 필요에 따라 객체 설정
                .build();

        when(couponClient.couponPolicyCreate(any(RequestCouponPolicyDTO.class)))
                .thenReturn(ResponseEntity.status(HttpStatus.OK).body("Success"));

        mockMvc.perform(MockMvcRequestBuilders.post("/admin/policy/create")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestCouponPolicyDTO)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/coupon/create"));
    }

    @Test
    void testUserCouponCreate() throws Exception {
        // 사용자 쿠폰 생성 요청
        RequestUserCouponDTO requestUserCouponDTO = RequestUserCouponDTO.builder()
                .userIds(List.of("user1", "user2"))
                .couponPolicyId(1L)
                .state(CouponState.LIVE)
                .startDate(LocalDate.now())
                .build();

        when(couponClient.userCouponCreate(any(RequestUserCouponDTO.class)))
                .thenReturn(ResponseEntity.status(HttpStatus.OK).body("Success"));

        mockMvc.perform(MockMvcRequestBuilders.post("/admin/coupon/distribution")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestUserCouponDTO)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/coupon/create"));
    }

    @Test
    void testUserCouponUpdate() throws Exception {
        // 사용자 쿠폰 수정 요청
        UpdateUserCouponDto updateUserCouponDto = new UpdateUserCouponDto();
        updateUserCouponDto.setUpdateUserIds(List.of(1L, 2L));
        updateUserCouponDto.setUpdatePolicyId(1L);
        updateUserCouponDto.setUpdateState(CouponState.USED);
        updateUserCouponDto.setUpdateEndDate(LocalDate.now().plusDays(10));

        when(couponClient.couponUpdate(any(UpdateUserCouponDto.class)))
                .thenReturn(ResponseEntity.status(HttpStatus.OK).body("Success"));

        mockMvc.perform(MockMvcRequestBuilders.post("/admin/coupon/update")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(updateUserCouponDto)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/coupon/create"));
    }

    @Test
    void testErrorHandlingInExecuteWithLogging() throws Exception {
        // 오류 처리 확인 (쿠폰 생성 실패 시)
        when(couponClient.couponPolicyCreate(any(RequestCouponPolicyDTO.class)))
                .thenThrow(new RuntimeException("Error during coupon policy creation"));

        RequestCouponPolicyDTO requestCouponPolicyDTO = RequestCouponPolicyDTO.builder()
                .policyName("Test Policy")
                .saleType(SaleType.PERCENT)
                .discountRuleDTO(new DiscountRuleDTO()) // 필요에 따라 객체 설정
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/admin/policy/create")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestCouponPolicyDTO)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/coupon/create?error=true"));
    }
}
