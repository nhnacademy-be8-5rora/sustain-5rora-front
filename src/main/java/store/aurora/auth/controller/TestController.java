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

    private static final String SEARCH_FIELD = "title";
    private static final String ASCENDING_ORDER = "asc";
    private static final String PAGE_SIZE = "1";


    @GetMapping("/")
    public String indexTest(Model model){
        //좋아요가 많은 책
        Page<BookSearchResponseDTO> likeBooks =
                bookSearchClient.searchBooksByKeyword("", SEARCH_FIELD, "", PAGE_SIZE, "like", ASCENDING_ORDER);
        //조회수가 많은 책
        Page<BookSearchResponseDTO> viewBooks =
                bookSearchClient.searchBooksByKeyword("", SEARCH_FIELD, "", PAGE_SIZE, "view", ASCENDING_ORDER);

        //저번달 기준 가장 많이 팔린 책
        ResponseEntity<BookSearchResponseDTO> mostSellerBookResponse = bookClient.getMostBook();

        model.addAttribute("mostSellerBook", mostSellerBookResponse.getBody());
        model.addAttribute("likeBooks", likeBooks.getContent());
        model.addAttribute("viewBooks", viewBooks.getContent());
        return "home";
    }
}
