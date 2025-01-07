package store.aurora.point.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import store.aurora.feign_client.point.PointPolicyClient;
import store.aurora.point.dto.PointPolicy;
import store.aurora.point.dto.PointPolicyRequest;
import store.aurora.point.dto.PointPolicyUpdateRequest;

import java.util.List;

@Controller
@RequestMapping("/admin/point-policies")
@RequiredArgsConstructor
public class PointPolicyController {
    private static final String REDIRECT_POINT_POLICIES = "redirect:/admin/point-policies";
    private final PointPolicyClient pointPolicyClient;

    @GetMapping
    public String getPointPolicies(Model model) {
        List<PointPolicy> pointPolicies = pointPolicyClient.getAllPointPolicies();
        model.addAttribute("pointPolicies", pointPolicies);
        return "admin/point-policy/point-policies";
    }

    @PatchMapping("/{id}")
    public String updatePointPolicy(@PathVariable Integer id,
                                    @Valid @ModelAttribute PointPolicyUpdateRequest request) {
        pointPolicyClient.updatePointPolicy(id, request);
        return REDIRECT_POINT_POLICIES;
    }

    @PatchMapping("/{id}/toggle-status")
    public String togglePointPolicyStatus(@PathVariable Integer id) {
        pointPolicyClient.updatePointPolicyActive(id);
        return REDIRECT_POINT_POLICIES; // 상태 변경 후 목록으로 리다이렉트
    }

    @GetMapping("/add")
    public String showAddPointPolicyForm() {
        return "admin/point-policy/point-policy-form";
    }

    @PostMapping("/add")
    public String addPointPolicy(@ModelAttribute PointPolicyRequest request) {
        pointPolicyClient.addPointPolicy(request);
        return REDIRECT_POINT_POLICIES;
    }
}