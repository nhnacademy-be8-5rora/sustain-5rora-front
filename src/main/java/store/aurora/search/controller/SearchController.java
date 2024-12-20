package store.aurora.search.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import store.aurora.feignClient.BookSearchClient;
import store.aurora.search.Page;
import store.aurora.search.dto.BookSearchResponseDTO;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class SearchController {

    private final BookSearchClient bookSearchClient;

    // 디버그용 요청 예시: http://localhost:8083/api/books/search?type=title&keyword=한강&pageNum=1&orderBy=salePrice&orderDirection=desc
    @GetMapping("/books/search")
    public String search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "1") String pageNum,
            @RequestParam(required = false) String orderBy,
            @RequestParam(required = false) String orderDirection,
            Model model) {

        try {
            // keyword 인코딩
            String encodedKeyword = URLEncoder.encode(keyword, "UTF-8");

            int page = Integer.parseInt(pageNum) - 1; // 페이지 번호 0-based
            Page<BookSearchResponseDTO> bookSearchResponseDTOPage = bookSearchClient.searchBooksByKeyword(
                    type, encodedKeyword, pageNum, orderBy, orderDirection
            );
            List<BookSearchResponseDTO> books = bookSearchResponseDTOPage.getContent();

            // 검색 결과와 페이징 정보를 모델에 추가
            model.addAttribute("books", books);
            model.addAttribute("currentPage", page + 1); // 현재 페이지 (1-based)
            model.addAttribute("totalPages", bookSearchResponseDTOPage.getTotalPages()); // 전체 페이지 수
            model.addAttribute("keyword", keyword);
            model.addAttribute("type", type);
            model.addAttribute("orderBy", orderBy);
            model.addAttribute("orderDirection", orderDirection);

            return "search/bookSearchResults"; // templates/search/bookSearchResults.html 반환
        } catch (UnsupportedEncodingException e) {
            // 인코딩 에러 처리
            model.addAttribute("error", "잘못된 문자 인코딩이 포함되어 있습니다.");
            return "error";
        }
    }
}
