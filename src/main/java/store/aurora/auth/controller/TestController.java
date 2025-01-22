package store.aurora.auth.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import store.aurora.common.annotation.Auth;
import store.aurora.feign_client.search.BookSearchClient;
import store.aurora.feign_client.book.BookClient;
import org.springframework.data.domain.Page;
import store.aurora.search.dto.BookSearchResponseDTO;

import java.util.Collections;

@Controller
@RequiredArgsConstructor
public class TestController {
    private final BookSearchClient bookSearchClient;
    private final BookClient bookClient;
    private static final Logger USER_LOG = LoggerFactory.getLogger("user-logger");

    private static final String SEARCH_FIELD = "title";
    private static final String ASCENDING_ORDER = "desc";

    @GetMapping("/")
    public String indexTest(Model model) {
        Page<BookSearchResponseDTO> likeBooks;
        Page<BookSearchResponseDTO> viewBooks;
        ResponseEntity<BookSearchResponseDTO> mostSellerBookResponse;

        try {
            likeBooks = bookSearchClient.searchBooksByKeyword("", SEARCH_FIELD, "", 0, "like", ASCENDING_ORDER);
        } catch (Exception e) {
            USER_LOG.error("Failed to fetch likeBooks: {}", e.getMessage(), e);
            likeBooks = Page.empty();
        }

        try {
            viewBooks = bookSearchClient.searchBooksByKeyword("", SEARCH_FIELD, "", 0, "view", ASCENDING_ORDER);
        } catch (Exception e) {
            USER_LOG.error("Failed to fetch viewBooks: {}", e.getMessage(), e);
            viewBooks = Page.empty();
        }

        try {
            mostSellerBookResponse = bookClient.findMostSoldByLastMonth();
        } catch (Exception e) {
            USER_LOG.error("Failed to fetch mostSellerBook: {}", e.getMessage(), e);
            mostSellerBookResponse = ResponseEntity.ofNullable(null);
        }

        model.addAttribute("likeBooks", likeBooks.isEmpty() ? Collections.emptyList() : likeBooks.getContent());
        model.addAttribute("viewBooks", viewBooks.isEmpty() ? Collections.emptyList() : viewBooks.getContent());

        BookSearchResponseDTO mostSellerBook = mostSellerBookResponse.getBody();
        if (mostSellerBook == null) {
            USER_LOG.warn("No mostSellerBook found.");
            model.addAttribute("mostSellerBook", new BookSearchResponseDTO());
        } else {
            model.addAttribute("mostSellerBook", mostSellerBook);
        }

        return "home";
    }

}
