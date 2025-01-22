package store.aurora.search.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import store.aurora.book.dto.category.CategoryResponseDTO;
import store.aurora.common.JwtUtil;
import store.aurora.feign_client.book.CategoryClient;
import store.aurora.feign_client.search.BookSearchClient;
import org.springframework.data.domain.Page;
import store.aurora.feign_client.search.ElasticSearchClient;
import store.aurora.search.dto.BookSearchResponseDTO;
import store.aurora.search.service.SearchService;

import java.util.Collections;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class SearchController {

    private final BookSearchClient bookSearchClient;
    private final ElasticSearchClient elasticSearchClient;
    private final CategoryClient categoryClient;
    private final SearchService searchService;

    // 디버그용 요청 예시: http://5rora-test:8080/books/search?type=title&keyword=한강&pageNum=1&orderBy=salePrice&orderDirection=desc
    @GetMapping("/books/search")
    public String search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "1") String pageNum,
            @RequestParam(required = false) String orderBy,
            @RequestParam(required = false) String orderDirection,
            HttpServletRequest request,  // HttpServletRequest 추가
            Model model) {

        // jwtToken이 없으면 빈 문자열로 설정
        String jwt = JwtUtil.getJwtFromCookie(request);

        if (jwt.equals("Bearer null")) {
            jwt = "";  // jwt가 null일 경우 빈 문자열 설정
        }

        if (keyword == null || keyword.trim().isEmpty()) {
            keyword = "";  // 빈 문자열로 설정하여 모든 책 검색
        }

        int page;
        try {
            page = Integer.parseInt(pageNum) - 1; // 페이지 번호 0-based
            if (page < 0) throw new IllegalArgumentException("pageNum은 0 이상의 정수여야 합니다.");
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", "잘못된 페이지 번호입니다.");
            return "error"; // 오류 페이지 반환
        }

        Page<BookSearchResponseDTO> bookSearchResponseDTOPage = searchService.searchBooks(
                jwt, type, keyword, page,orderBy,orderDirection
        );

        // 검색 결과가 없으면 에러 페이지 대신 메시지 전달
        if (bookSearchResponseDTOPage == null || bookSearchResponseDTOPage.getTotalElements() == 0) {
            model.addAttribute("message", "\"" + keyword + "\"로 검색된 결과가 없습니다.");
            model.addAttribute("books", Collections.emptyList()); // 빈 리스트 설정

        } else {
            List<BookSearchResponseDTO> books = bookSearchResponseDTOPage.getContent();

            if ("category".equals(type)) {
                ResponseEntity<List<CategoryResponseDTO>> response = categoryClient.getCategories();

                if (response.getStatusCode().is2xxSuccessful()) {
                    List<CategoryResponseDTO> categories = response.getBody();
                    model.addAttribute("categories", categories);
                    // 데이터를 처리
                } else {
                    throw new IllegalArgumentException("Failed to fetch categories. Status: " + response.getStatusCode());
                }

            }
            // 검색 결과와 페이징 정보를 모델에 추가
            model.addAttribute("books", books);
            model.addAttribute("currentPage", page + 1); // 현재 페이지 (1-based)
            model.addAttribute("totalPages", bookSearchResponseDTOPage.getTotalPages()); // 전체 페이지 수
        }
        model.addAttribute("keyword", keyword);
        model.addAttribute("type", type);
        model.addAttribute("orderBy", orderBy);
        model.addAttribute("orderDirection", orderDirection);

        return "search/bookSearchResults"; // templates/search/bookSearchResults.html 반환
    }
}