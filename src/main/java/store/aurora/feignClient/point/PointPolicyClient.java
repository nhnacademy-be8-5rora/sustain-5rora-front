package store.aurora.feignClient.point;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import store.aurora.config.FeignConfig;
import store.aurora.point.dto.PointPolicy;
import store.aurora.point.dto.PointPolicyRequest;
import store.aurora.point.dto.PointPolicyUpdateRequest;

import java.util.List;

@FeignClient(name = "pointPolicyClient", url = "${api.gateway.base-url}" + "/api/points/policies", configuration = FeignConfig.class)
public interface PointPolicyClient {

    @GetMapping
    List<PointPolicy> getAllPointPolicies();

    @PatchMapping("/{id}")
    void updatePointPolicy(@PathVariable("id") Integer id, @RequestBody PointPolicyUpdateRequest request);

    @PostMapping
    void addPointPolicy(@RequestBody PointPolicyRequest request);
}