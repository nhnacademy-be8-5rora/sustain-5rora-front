package store.aurora.feign_client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import store.aurora.user.dto.UserRank;

import java.util.List;

@FeignClient(name = "userRankClient", url = "${api.gateway.base-url}")
public interface UserRankClient {
    @GetMapping("/api/user-ranks")
    List<UserRank> getAllUserRanks();
}