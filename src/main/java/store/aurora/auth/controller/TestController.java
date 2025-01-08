package store.aurora.auth.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import store.aurora.common.annotation.Auth;
import store.aurora.feign_client.BookSearchClient;
import store.aurora.feign_client.book.BookClient;
import org.springframework.data.domain.Page;
import store.aurora.search.dto.BookSearchResponseDTO;

@Controller
@RequiredArgsConstructor
public class TestController {
    private final BookSearchClient bookSearchClient;
    private final BookClient bookClient;
    private static final Logger USER_LOG = LoggerFactory.getLogger("user-logger");

    //todo 제거 예정
    @GetMapping("/login-test")
    public String userIdTest(@Auth String username, Model model){
        model.addAttribute("username", username);
        return "login-test";
    }

    @GetMapping("/")
    public String indexTest(Model model){
        Page<BookSearchResponseDTO> likeBooks =
                bookSearchClient.searchBooksByKeyword("","title","","1","like","asc");
        Page<BookSearchResponseDTO> viewBooks =
                bookSearchClient.searchBooksByKeyword("","title","","1","view","asc");
        Page<BookSearchResponseDTO> bestSellBook=bookSearchClient.searchBooksByKeyword("","title","","1","bestsell","asc");
        ResponseEntity<BookSearchResponseDTO> mostSellerBookResponse= bookClient.getMostBook();
        model.addAttribute("mostSellerBook", mostSellerBookResponse.getBody());
        model.addAttribute("likeBooks", likeBooks.getContent());
        model.addAttribute("viewBooks", viewBooks.getContent());
        return "home";
    }
}
