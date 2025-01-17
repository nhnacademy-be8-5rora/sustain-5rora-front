package store.aurora.feign_client.order;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import store.aurora.order.dto.WrapResponseDTO;

import java.util.List;

@FeignClient(name = "wrapClient", url = "${api.gateway.base-url}" + "/api/order/wrap")
public interface WrapClient {

    @GetMapping("/get-wrap-list")
    ResponseEntity<List<WrapResponseDTO>> getWrapList();
}
