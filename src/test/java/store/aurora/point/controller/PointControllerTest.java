package store.aurora.point.controller;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import store.aurora.config.security.constants.SecurityConstants;
import store.aurora.feign_client.point.PointHistoryClient;
import store.aurora.point.dto.PointHistory;
import store.aurora.point.dto.PointHistoryResponse;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PointControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PointHistoryClient pointFeignClient;

    @InjectMocks
    private PointController pointController;

    private PointHistory pointHistory;
    private PointHistory pointHistory2;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(pointController).build();

        pointHistory = new PointHistory();
        pointHistory.setId(1L);
        pointHistory.setPointAmount(100);
        pointHistory.setPointType("EARNED");
        pointHistory.setTransactionDate(LocalDateTime.now());
        pointHistory.setFrom("22342341"); // 주문 번호

        pointHistory2 = new PointHistory();
        pointHistory2.setId(2L);
        pointHistory2.setPointAmount(-50);
        pointHistory2.setPointType("USED");
        pointHistory2.setTransactionDate(LocalDateTime.now());
        pointHistory2.setFrom("결제");
    }

    @Test
    void testGetPointsPage() throws Exception {
        // Arrange
        String jwt = "test-jwt";
        Cookie jwtCookie = new Cookie(SecurityConstants.TOKEN_COOKIE_NAME, jwt);

        List<PointHistory> pointHistories = List.of(pointHistory, pointHistory2);

        PointHistoryResponse historyResponse = new PointHistoryResponse();
        historyResponse.setContent(pointHistories);
        historyResponse.setTotalPages(1);
        historyResponse.setFirst(true);
        historyResponse.setLast(true);

        when(pointFeignClient.getPointHistory(SecurityConstants.BEARER_TOKEN_PREFIX + jwt, 0, 10))
                .thenReturn(historyResponse);
        when(pointFeignClient.getAvailablePoints(SecurityConstants.BEARER_TOKEN_PREFIX + jwt))
                .thenReturn(200);

        // Act & Assert
        mockMvc.perform(get("/mypage/points")
                        .cookie(jwtCookie)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(view().name("mypage/point-history"))
                .andExpect(model().attributeExists("pointHistory"))
                .andExpect(model().attributeExists("availablePoints"))
                .andExpect(model().attributeExists("currentPage"))
                .andExpect(model().attributeExists("totalPages"))
                .andExpect(model().attribute("pointHistory", pointHistories))
                .andExpect(model().attribute("availablePoints", 200))
                .andExpect(model().attribute("currentPage", 0))
                .andExpect(model().attribute("totalPages", 1))
                .andExpect(model().attribute("isFirst", true))
                .andExpect(model().attribute("isLast", true));

        // Verify interactions
        verify(pointFeignClient, times(1)).getPointHistory(SecurityConstants.BEARER_TOKEN_PREFIX + jwt, 0, 10);
        verify(pointFeignClient, times(1)).getAvailablePoints(SecurityConstants.BEARER_TOKEN_PREFIX + jwt);
    }
}