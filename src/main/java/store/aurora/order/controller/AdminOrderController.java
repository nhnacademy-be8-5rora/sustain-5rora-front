package store.aurora.order.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import store.aurora.common.encryptor.SimpleEncryptor;
import store.aurora.feign_client.order.AdminOrderClient;
import store.aurora.order.dto.AdminOrderDTO;


@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/order")
public class AdminOrderController {

    private final AdminOrderClient adminOrderClient;
    private final SimpleEncryptor simpleEncryptor;

    @GetMapping
    public String orderManagementPage(@RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "10") int size,
                                      Model model) {
        Page<AdminOrderDTO> orderPage = adminOrderClient.getAllOrderList(page, size);

        model.addAttribute("orderList", orderPage.getContent());
        model.addAttribute("currentPage", orderPage.getNumber());
        model.addAttribute("totalPages", orderPage.getTotalPages());

        return "admin/order/shipment";
    }

    @PostMapping("/shipment-update")
    public String updateShipmentStatus(@RequestParam("order-id") Long orderId,
                                       @RequestParam("shipment-state") String shipmentState){

        adminOrderClient.updateShipmentStatus(orderId, shipmentState);

        return "redirect:/admin/order/shipment";
    }

    @PostMapping("/refund-confirm")
    public String refundConfirm(@RequestParam("order-id") Long orderId){
        adminOrderClient.resolveRefund(simpleEncryptor.encrypt(String.valueOf(orderId)));

        return "redirect:/admin/order/shipment";
    }
}
