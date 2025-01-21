package store.aurora.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import store.aurora.feign_client.UserRankClient;
import store.aurora.user.dto.UserRank;

import java.util.List;

@Controller
@RequestMapping("/mypage/user-ranks")
@RequiredArgsConstructor
public class UserRankController {
    private final UserRankClient userRankClient;

    @GetMapping
    public String getAllUserRanks(Model model) {
        List<UserRank> userRanks = userRankClient.getAllUserRanks();
        model.addAttribute("userRanks", userRanks);
        return "mypage/user-ranks";
    }
}