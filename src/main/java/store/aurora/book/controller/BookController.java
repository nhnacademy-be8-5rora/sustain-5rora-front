package store.aurora.book.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import store.aurora.book.dto.BookDetailsDto;
import store.aurora.book.dto.aladin.BookDto;
import store.aurora.book.dto.aladin.BookRequestDtoEx;
import store.aurora.feignClient.book.BookClient;

import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {

    private final BookClient bookClient;

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
        model.addAttribute("book", response.getBody());
        return "/admin/book/api/register";
    }

    // API 도서 등록 처리
    @PostMapping("/api/register")
    public String registerApiBook(@ModelAttribute BookRequestDtoEx bookRequestDto) {
        bookClient.registerApiBook(bookRequestDto);
        return "redirect:/books"; // 검색 페이지로 리다이렉트
    }

    // 직접 등록 폼 렌더링
    @GetMapping("/direct/register")
    public String showDirectRegisterForm(Model model) {
        model.addAttribute("book", new BookRequestDtoEx()); // 빈 객체 전달
        return "/admin/book/api/direct-register"; // 직접 등록 페이지 템플릿 경로
    }

    // 직접 도서 등록 처리
    @PostMapping("/direct/register")
    public String registerDirectBook(@ModelAttribute BookRequestDtoEx bookRequestDto) {
        bookClient.registerDirectBook(bookRequestDto);
        return "redirect:/books"; // 검색 페이지로 리다이렉트
    }

    @GetMapping("/{bookId}")
    public String singleInquiryBookInfo(@PathVariable Long bookId, Model model) {
        BookDetailsDto book = bookClient.getBookDetails(bookId).getBody();
        model.addAttribute("bookInfo", book);

        return "home/home";
    }
}
