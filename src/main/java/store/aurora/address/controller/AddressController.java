package store.aurora.address.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import store.aurora.address.dto.UserAddressDTO;
import store.aurora.address.dto.UserAddressRequest;
import store.aurora.common.JwtUtil;
import store.aurora.feign_client.AddressClient;

import java.util.List;

@Controller
@RequestMapping("/mypage/addresses")
@RequiredArgsConstructor
public class AddressController {
    private final AddressClient addressFeignClient;

    @GetMapping
    public String showAddresses(HttpServletRequest request,
                                Model model) {
        String jwt = JwtUtil.getJwtFromCookie(request);
        List<UserAddressDTO> addresses = addressFeignClient.getUserAddresses(jwt);
        model.addAttribute("addresses", addresses);
        return "mypage/address-list";
    }

    @GetMapping("/new")
    public String showAddressForm(Model model) {
        model.addAttribute("address", new UserAddressDTO());
        return "mypage/address-form";
    }

    @PostMapping("/new")
    public String addAddress(HttpServletRequest request,
                             @ModelAttribute UserAddressRequest userAddressRequest) {
        String jwt = JwtUtil.getJwtFromCookie(request);
        addressFeignClient.addUserAddress(jwt, userAddressRequest);
//        model.addAttribute("success", "배송지가 성공적으로 추가되었습니다.");
//        } catch (Exception e) {model.addAttribute("error", "배송지 추가에 실패했습니다.");}
        return "redirect:/mypage/addresses";
    }

    @GetMapping("/{id}")
    public String showEditForm(HttpServletRequest request,
                               @PathVariable Long id,
                               Model model) {
        String jwt = JwtUtil.getJwtFromCookie(request);
        UserAddressDTO address = addressFeignClient.getAddressById(jwt, id);
//        model.addAttribute("id", id);
        model.addAttribute("address", address);
        return "mypage/address-form";
    }

    @PostMapping("/delete")
    public String deleteAddress(HttpServletRequest request, @RequestParam("id") Long id) {
        String jwt = JwtUtil.getJwtFromCookie(request);
        addressFeignClient.deleteUserAddress(id, jwt);
        return "redirect:/mypage/addresses";
    }

    @PostMapping("/{id}")
    public String updateAddress(HttpServletRequest request,
                                @PathVariable Long id,
                                @ModelAttribute UserAddressRequest userAddressDTO) {
        String jwt = JwtUtil.getJwtFromCookie(request);
        addressFeignClient.updateAddress(id, jwt, userAddressDTO);
        return "redirect:/mypage/addresses";
    }
}