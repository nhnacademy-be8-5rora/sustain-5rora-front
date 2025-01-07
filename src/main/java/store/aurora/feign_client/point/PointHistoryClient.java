package store.aurora.feign_client.point;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import store.aurora.config.security.constants.SecurityConstants;
import store.aurora.point.dto.PointHistoryResponse;

@FeignClient(name = "pointClient", url = "${api.gateway.base-url}" + "/api/points/history")
public interface PointHistoryClient {

    @GetMapping
    PointHistoryResponse getPointHistory(
            @RequestHeader(SecurityConstants.AUTHORIZATION_HEADER) String jwtToken,
            @RequestParam("page") int page,
            @RequestParam("size") int size
    );

    @GetMapping("/available")
    Integer getAvailablePoints(@RequestHeader(SecurityConstants.AUTHORIZATION_HEADER) String jwtToken);
}