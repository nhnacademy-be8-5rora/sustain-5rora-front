package store.aurora.book.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import store.aurora.book.dto.*;
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

    // 책 상세 조회
//    @GetMapping("/{bookId}")
//    public String getBookDetails(@PathVariable Long bookId, Model model) {
//        ResponseEntity<BookDetailsDto> response = bookClient.getBookDetails(bookId);
//        model.addAttribute("book", response.getBody());
//        return "book/details"; // 뷰 이름 (예: book/details.html)
//    }
    @GetMapping
    public String listBooks(Model model) {
        // Book 리스트를 조회 (FeignClient를 활용해 데이터 가져오기)
//        List<BookResponseDTO> books = bookClient.getBooks(); // 이 메서드가 정의되어야 합니다.
//        model.addAttribute("books", books);
        return "admin/book/list"; // books 리스트를 보여줄 HTML 뷰 이름
    }
    // 책 생성 폼 페이지
    @GetMapping("/create")
    public String createBookForm(Model model) {
        model.addAttribute("book", new BookRequestDTO());
//        List<CategoryResponseDTO> categories = categoryClient.getAllCategories().getBody();
//        model.addAttribute("categories", categories);
        return "admin/book/form"; // 뷰 이름 (예: book/form.html)
    }

    // 책 생성
    @PostMapping
    public String createBook(@ModelAttribute @Valid BookRequestDTO bookRequestDTO) {
        bookClient.createBook(bookRequestDTO);
        return "redirect:/books"; // 책 목록 페이지로 리디렉션
    }

    // 책 세부 수정 처리
    @PostMapping("/{bookId}/edit-details")
    public String updateBookDetails(@PathVariable Long bookId,
                                    @ModelAttribute @Valid BookDetailsUpdateDTO updateDTO) {
        bookClient.updateBookDetails(bookId, updateDTO);
        return "redirect:/books";
    }

    // 책 판매 정보 수정 폼 페이지
//    @GetMapping("/{bookId}/edit-sales-info")
//    public String editBookSalesInfoForm(@PathVariable Long bookId, Model model) {
//        BookResponseDTO book = bookClient.getBooks(0, 1).getBody().stream()
//                .filter(b -> b.getId().equals(bookId))
//                .findFirst()
//                .orElseThrow(() -> new RuntimeException("Book not found"));
//        model.addAttribute("bookSalesInfoUpdateDTO", new BookSalesInfoUpdateDTO());
//        model.addAttribute("book", book);
//        return "admin/book/edit-sales-info"; // 판매 정보 수정 페이지 뷰
//    }

    // 책 판매 정보 수정 처리
    @PostMapping("/{bookId}/edit-sales-info")
    public String updateBookSalesInfo(@PathVariable Long bookId,
                                      @ModelAttribute @Valid BookSalesInfoUpdateDTO salesInfoDTO) {
        bookClient.updateBookSalesInfo(bookId, salesInfoDTO);
        return "redirect:/books";
    }

    // 책 포장 여부 수정 처리
    @PatchMapping("/{bookId}/update-packaging")
    public String updateBookPackaging(@PathVariable Long bookId, @RequestParam boolean packaging) {
        bookClient.updateBookPackaging(bookId, packaging);
        return "redirect:/books";
    }

//    @GetMapping("/{bookId}")
//    public String singleInquiryBookInfo(@PathVariable Long bookId, Model model) {
//        BookDetailsDto book = bookClient.getBookDetails(bookId).getBody();
//        model.addAttribute("bookInfo", book);
//
//        return "home";
//    }

}



