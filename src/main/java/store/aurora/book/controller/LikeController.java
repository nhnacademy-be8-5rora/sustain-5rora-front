package store.aurora.book.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import store.aurora.common.JwtUtil;
import store.aurora.feignClient.book.LikeClient;

@Controller
@RequestMapping("/books")
@RequiredArgsConstructor
public class LikeController {

    private final LikeClient likeClient;
    @PostMapping("/likes/{bookId}")
    public void doLike(@PathVariable("bookId") String bookId,
                                         HttpServletRequest request) {
        String jwt = JwtUtil.getJwtFromCookie(request);
        likeClient.doLike(jwt,bookId);

    }
}
