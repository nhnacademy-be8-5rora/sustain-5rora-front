

package store.aurora.point.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import store.aurora.feign_client.point.PointPolicyClient;
import store.aurora.point.dto.PointPolicy;
import store.aurora.point.dto.PointPolicyCategory;
import store.aurora.point.dto.PointPolicyRequest;
import store.aurora.point.dto.PointPolicyUpdateRequest;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PointPolicyControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PointPolicyClient pointPolicyClient;

    @InjectMocks
    private PointPolicyController pointPolicyController;

    private PointPolicy pointPolicy;
    private PointPolicyUpdateRequest updateRequest;
    private PointPolicyRequest newPolicyRequest;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(pointPolicyController).build();

        pointPolicy = new PointPolicy();
        pointPolicy.setId(1);
        pointPolicy.setPointPolicyCategory(PointPolicyCategory.ORDER);
        pointPolicy.setPointPolicyName("Test Policy");
        pointPolicy.setPointPolicyType("PERCENTAGE");
        pointPolicy.setPointPolicyValue(BigDecimal.valueOf(10));
        pointPolicy.setIsActive(true);

        updateRequest = new PointPolicyUpdateRequest();
        updateRequest.setPointPolicyValue(BigDecimal.valueOf(15));

        newPolicyRequest = new PointPolicyRequest(PointPolicyCategory.ORDER, "New Policy", "AMOUNT", BigDecimal.valueOf(100));
    }

    @Test
    void testGetPointPolicies() throws Exception {
        List<PointPolicy> policies = List.of(pointPolicy);
        when(pointPolicyClient.getAllPointPolicies()).thenReturn(policies);

        mockMvc.perform(get("/admin/point-policies"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/point-policy/point-policies"))
                .andExpect(model().attributeExists("pointPolicies"))
                .andExpect(model().attribute("pointPolicies", policies));

        verify(pointPolicyClient, times(1)).getAllPointPolicies();
    }

    @Test
    void testUpdatePointPolicy() throws Exception {
        mockMvc.perform(patch("/admin/point-policies/{id}", 1)
                        .flashAttr("pointPolicyUpdateRequest", updateRequest))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/point-policies"));

        verify(pointPolicyClient, times(1)).updatePointPolicy(eq(1), eq(updateRequest));
    }

    @Test
    void testTogglePointPolicyStatus() throws Exception {
        mockMvc.perform(patch("/admin/point-policies/{id}/toggle-status", 1))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/point-policies"));

        verify(pointPolicyClient, times(1)).updatePointPolicyActive(1);
    }

    @Test
    void testShowAddPointPolicyForm() throws Exception {
        mockMvc.perform(get("/admin/point-policies/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/point-policy/point-policy-form"));
    }

    @Test
    void testAddPointPolicy() throws Exception {
        mockMvc.perform(post("/admin/point-policies/add")
                        .flashAttr("pointPolicyRequest", newPolicyRequest))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/point-policies"));

        verify(pointPolicyClient, times(1)).addPointPolicy(eq(newPolicyRequest));
    }
}
