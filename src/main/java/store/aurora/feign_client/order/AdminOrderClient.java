package store.aurora.feign_client.order;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import store.aurora.order.dto.AdminOrderDTO;

import java.util.List;

@FeignClient(name="adminOrderClient", url = "${api.gateway.base-url}" + "/api/admin/order")
public interface AdminOrderClient {
    @GetMapping
    List<AdminOrderDTO> getAllOrderList();
}
