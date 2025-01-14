package store.aurora.order.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import store.aurora.feign_client.order.AdminOrderClient;
import store.aurora.order.dto.AdminOrderDTO;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/order")
public class AdminOrderController {

    private final AdminOrderClient adminOrderClient;

    @GetMapping("/shipment")
    public String shipmentManagementPage(Model model){
        List<AdminOrderDTO> orderList = adminOrderClient.getAllOrderList();

        model.addAttribute("orderList", orderList);

        return "admin/order/shipment";
    }
}
