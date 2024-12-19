package store.aurora.search.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import store.aurora.feignClient.BookSearchClient;
import store.aurora.search.dto.BookSearchResponseDTO;

@Controller
@RequiredArgsConstructor
public class SearchController {

    private final BookSearchClient bookSearchClient;


    // 디버그용 요청 예시: http://localhost:8083/api/books/search?type=title&keyword=한강&pageNum=1
    @GetMapping("/books/search")
    public String search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "1") String pageNum,
            Model model) {

        int page = Integer.parseInt(pageNum) - 1; // 페이지 번호 0-based
        BookSearchResponseDTO[] bookSearchResponseDTOPage = bookSearchClient.searchBooksByKeyword(type,keyword,pageNum);

        // 검색 결과와 페이징 정보를 모델에 추가
        model.addAttribute("books", bookSearchResponseDTOPage);
        model.addAttribute("currentPage", page + 1); // 현재 페이지 (1-based)

        model.addAttribute("keyword", keyword);
        model.addAttribute("type", type);

        return "search/bookSearchResults"; // templates/search/bookSearchResults.html 반환
    }
}
