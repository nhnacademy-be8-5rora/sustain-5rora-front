package store.aurora.point.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import store.aurora.feignClient.point.PointPolicyClient;
import store.aurora.point.dto.PointPolicy;
import store.aurora.point.dto.PointPolicyUpdateRequest;

import java.util.List;

@Controller
@RequestMapping("/admin/point-policies")
@RequiredArgsConstructor
public class PointPolicyController {
    private final PointPolicyClient pointPolicyClient;

    @GetMapping
    public String getPointPolicies(Model model) {
        List<PointPolicy> pointPolicies = pointPolicyClient.getAllPointPolicies();
        model.addAttribute("pointPolicies", pointPolicies);
        return "point-policies";
    }

    @PatchMapping("/{id}")
    public String updatePointPolicy(@PathVariable Integer id,
                                    @Valid @ModelAttribute PointPolicyUpdateRequest request) {
        pointPolicyClient.updatePointPolicy(id, request);
        return "redirect:/admin/point-policies";
    }
}