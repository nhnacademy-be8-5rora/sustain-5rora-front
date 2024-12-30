package store.aurora.book.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import store.aurora.book.dto.BookDetailsDto;
import store.aurora.book.dto.aladin.BookDto;
import store.aurora.book.dto.category.CategoryResponseDTO;
import store.aurora.feignClient.book.BookClient;
import store.aurora.feignClient.book.CategoryClient;

import java.util.List;

@Controller
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {

    private final BookClient bookClient;
    private final CategoryClient categoryClient;

    @GetMapping
    public String searchForm() {
        return "/admin/book/api/search";
    }

    @GetMapping("/api/search-books")
    public String searchBooks(@RequestParam String query,
                              @RequestParam String queryType,
                              @RequestParam String searchTarget,
                              @RequestParam(defaultValue = "1") int start,
                              Model model) {
        ResponseEntity<List<BookDto>> response = bookClient.searchBooks(query, queryType, searchTarget, start);
        List<BookDto> books = response.getBody(); // ResponseEntity에서 Body 추출
        model.addAttribute("books", books); // 검색 결과
        model.addAttribute("currentPage", start); // 현재 페이지
        model.addAttribute("query", query); // 검색어
        model.addAttribute("queryType", queryType); // 검색 유형
        model.addAttribute("searchTarget", searchTarget); // 검색 대상
        return "/admin/book/api/search";
    }

    // API 도서 등록 폼 렌더링
    @GetMapping("/api/register")
    public String showRegisterForm(@RequestParam String bookId, Model model) {
        ResponseEntity<BookDto> response = bookClient.getBookById(bookId);
        ResponseEntity<List<CategoryResponseDTO>> responseCategory = categoryClient.getCategoryHierarchy();
        model.addAttribute("categories", responseCategory.getBody());
        System.out.println("gg"+response);
        model.addAttribute("book", response.getBody());
        return "/admin/book/api/register";
    }

    // API 도서 등록 처리
    @PostMapping(value = "/api/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String registerApiBook(@ModelAttribute BookDto bookDto,
                                  @RequestParam(value = "uploadedImages", required = false) List<MultipartFile> additionalImages
    ) {
        bookClient.registerApiBook(bookDto,additionalImages);
        return "redirect:/books"; // 검색 페이지로 리다이렉트
    }

    // 직접 등록 폼 렌더링
    @GetMapping("/direct/register")
    public String showDirectRegisterForm(Model model) {
        ResponseEntity<List<CategoryResponseDTO>> responseCategory = categoryClient.getCategoryHierarchy();
        model.addAttribute("categories", responseCategory.getBody());
        model.addAttribute("book", new BookDto()); // 빈 객체 전달
        return "/admin/book/api/direct-register"; // 직접 등록 페이지 템플릿 경로
    }

    // 직접 도서 등록 처리
    @PostMapping(value = "/direct/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String registerDirectBook(
            @ModelAttribute BookDto bookDto,
            @RequestPart(value = "coverImage", required = false) MultipartFile coverImage,
            @RequestParam(value = "additionalImages", required = false) List<MultipartFile> additionalImages

    ) {
        // Feign 클라이언트를 통해 데이터 전달
        bookClient.registerDirectBook(bookDto, coverImage,additionalImages);
        return "redirect:/books"; // 검색 페이지로 리다이렉트
    }


    @GetMapping("/{bookId}")
    public String singleInquiryBookInfo(@PathVariable Long bookId, Model model) {
        BookDetailsDto book = bookClient.getBookDetails(bookId).getBody();
        model.addAttribute("bookInfo", book);

        return "home/home";
    }
}
