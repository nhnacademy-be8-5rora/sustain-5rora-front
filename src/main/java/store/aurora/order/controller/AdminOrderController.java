package store.aurora.order.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import store.aurora.feign_client.order.AdminOrderClient;
import store.aurora.order.dto.AdminOrderDTO;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/order")
public class AdminOrderController {

    private final AdminOrderClient adminOrderClient;

    @GetMapping("/shipment")
    public String shipmentManagementPage(@RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "10") int size,
                                         Model model) {
        Page<AdminOrderDTO> orderPage = adminOrderClient.getAllOrderList(page, size);

        model.addAttribute("orderList", orderPage.getContent());
        model.addAttribute("currentPage", orderPage.getNumber());
        model.addAttribute("totalPages", orderPage.getTotalPages());

        return "admin/order/shipment";
    }

//    @PostMapping("/shipment")
//    public String updateShipmentStatus(Long orderId, String shipmentState){
//        adminOrderClient.updateShipmentStatus(orderId, shipmentState);
//
//        return "redirect:/admin/order/shipment";
//    }
}
