package store.aurora.order.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import store.aurora.feign_client.order.WrapClient;
import store.aurora.order.dto.WrapResponseDTO;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/wrap")
public class AdminWrapController {
    private final WrapClient wrapClient;

    @GetMapping
    public String getWrapList(Model model){
        List<WrapResponseDTO> wrapList = wrapClient.getWrapList().getBody();

        model.addAttribute("wrapList", wrapList);

        return "admin/order/wrap";
    }

    @PostMapping("/update-wrap")
    public String updateWrap(WrapResponseDTO wrap){
        wrapClient.updateWrap(wrap);

        return "redirect:/admin/wrap";
    }
}
