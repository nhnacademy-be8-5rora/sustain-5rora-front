package store.aurora.user.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import store.aurora.feign_client.UserRankClient;
import store.aurora.user.domain.Rank;
import store.aurora.user.dto.UserRank;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserRankControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserRankClient userRankClient;

    @InjectMocks
    private UserRankController userRankController;

    private UserRank userRank1;
    private UserRank userRank2;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userRankController).build();

        userRank1 = new UserRank(1L, Rank.GENERAL, 0, 1000, BigDecimal.valueOf(0.01));
        userRank2 = new UserRank(2L, Rank.GOLD, 1001, 5000, BigDecimal.valueOf(0.02));
    }

    @Test
    void testGetAllUserRanks() throws Exception {
        // Arrange
        List<UserRank> userRanks = List.of(userRank1, userRank2);
        when(userRankClient.getAllUserRanks()).thenReturn(userRanks);

        // Act & Assert
        mockMvc.perform(get("/mypage/user-ranks"))
                .andExpect(status().isOk())
                .andExpect(view().name("mypage/user-ranks"))
                .andExpect(model().attributeExists("userRanks"))
                .andExpect(model().attribute("userRanks", userRanks));

        // Verify
        verify(userRankClient, times(1)).getAllUserRanks();
    }
}