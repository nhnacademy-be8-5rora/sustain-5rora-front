package store.aurora.feign_client.order;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import store.aurora.order.dto.AdminOrderDTO;

import java.util.List;

@FeignClient(name="adminOrderClient", url = "${api.gateway.base-url}" + "/api/admin/order")
public interface AdminOrderClient {
    @GetMapping
    Page<AdminOrderDTO> getAllOrderList(@RequestParam("page") int page, @RequestParam("size") int size);


//    @PostMapping("/shipment-update/{orderId}")
//    void updateShipmentStatus(Long orderId, String shipmentState);
}
