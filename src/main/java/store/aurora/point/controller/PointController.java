package store.aurora.point.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import store.aurora.common.JwtUtil;
import store.aurora.feign_client.point.PointHistoryClient;
import store.aurora.point.dto.PointHistoryResponse;

@Controller
@RequiredArgsConstructor
public class PointController {

    private final PointHistoryClient pointFeignClient;

    @GetMapping("/mypage/points")
    public String getPointsPage(
            HttpServletRequest request,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            Model model
    ) {
        String jwt = JwtUtil.getJwtFromCookie(request);
        PointHistoryResponse historyResponse = pointFeignClient.getPointHistory(jwt, page, size);
        Integer availablePoints = pointFeignClient.getAvailablePoints(jwt);

        model.addAttribute("pointHistory", historyResponse.getContent());
        model.addAttribute("availablePoints", availablePoints);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", historyResponse.getTotalPages());
        model.addAttribute("isFirst", historyResponse.isFirst());
        model.addAttribute("isLast", historyResponse.isLast());
        return "mypage/point-history";
    }
}