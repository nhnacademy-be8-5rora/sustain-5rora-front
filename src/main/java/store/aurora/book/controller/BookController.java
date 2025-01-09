package store.aurora.book.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import store.aurora.book.dto.BookDetailsDto;
import store.aurora.book.dto.aladin.AladinBookDto;
import store.aurora.book.dto.aladin.BookDetailDto;
import store.aurora.book.dto.aladin.BookRequestDto;
import store.aurora.book.dto.aladin.BookResponseDto;
import store.aurora.book.dto.category.CategoryResponseDTO;
import store.aurora.common.JwtUtil;
import store.aurora.feign_client.book.AladinBookClient;
import store.aurora.feign_client.book.BookClient;
import store.aurora.feign_client.book.CategoryClient;
import store.aurora.search.dto.BookSearchResponseDTO;

import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {

    private final BookClient bookClient;
    private final AladinBookClient aladinBookClient;
    private final CategoryClient categoryClient;

    @GetMapping("/aladin/search/form")
    public String searchForm() {
        return "admin/book/aladin/search";
    }

    @GetMapping("/aladin/search")
    public String searchBooks(@RequestParam String query,
                              @RequestParam(defaultValue = "Keyword") String queryType,
                              @RequestParam(defaultValue = "Book") String searchTarget,
                              @RequestParam(defaultValue = "1", required = false) int start,
                              Model model) {
        ResponseEntity<List<AladinBookDto>> response = aladinBookClient.searchBooks(query, queryType, searchTarget, start);
        List<AladinBookDto> books = response.getBody(); // ResponseEntity에서 Body 추출
        model.addAttribute("books", books); // 검색 결과
        model.addAttribute("currentPage", start); // 현재 페이지
        model.addAttribute("query", query); // 검색어
        model.addAttribute("queryType", queryType); // 검색 유형
        model.addAttribute("searchTarget", searchTarget); // 검색 대상
        return "admin/book/aladin/search";
    }

    // API 도서 등록 폼 렌더링
    @GetMapping("/aladin/register")
    public String showRegisterForm(@RequestParam String isbn13, Model model) {

        ResponseEntity<AladinBookDto> response = aladinBookClient.getBookDetailsByIsbn(isbn13);
        ResponseEntity<List<CategoryResponseDTO>> responseCategory = categoryClient.getCategories();
        model.addAttribute("book", response.getBody());
        model.addAttribute("categories", responseCategory.getBody());
        return "admin/book/aladin/aladin-register";
    }

    // API 도서 등록 처리
    @PostMapping(value = "/aladin/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String registerApiBook(@Valid @ModelAttribute BookRequestDto bookDto,
                                  @RequestPart(value = "additionalImages", required = false) List<MultipartFile> additionalImages
    ) {
        aladinBookClient.registerApiBook(bookDto,additionalImages);
        return "redirect:/books";
    }

    // 직접 등록 폼 렌더링
    @GetMapping("/register")
    public String showDirectRegisterForm(Model model) {
        ResponseEntity<List<CategoryResponseDTO>> responseCategory = categoryClient.getCategories();
        model.addAttribute("categories", responseCategory.getBody());
        model.addAttribute("book", new BookRequestDto()); // 빈 객체 전달
        return "admin/book/register";
    }

    // 직접 도서 등록 처리
    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String registerDirectBook(
            @Valid @ModelAttribute BookRequestDto bookDto,
            @RequestPart(value = "coverImage", required = false) MultipartFile coverImage,
            @RequestPart(value = "additionalImages", required = false) List<MultipartFile> additionalImages) {
        // Feign 클라이언트를 통해 데이터 전달
        bookClient.registerDirectBook(bookDto, coverImage,additionalImages);
        return "redirect:/books";
    }

    @GetMapping
    public String listBooks(@RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "2") int size,
                            Model model) {
        ResponseEntity<Page<BookResponseDto>> response = bookClient.getAllBooks(page, size);
        Page<BookResponseDto> bookPage = response.getBody();

        if (bookPage != null) {
            model.addAttribute("books", bookPage.getContent());
            model.addAttribute("currentPage", bookPage.getNumber());
            model.addAttribute("totalPages", bookPage.getTotalPages());
        } else {
            model.addAttribute("books", Collections.emptyList());
            model.addAttribute("currentPage", 0);
            model.addAttribute("totalPages", 0);
        }
        return "admin/book/book-list";
    }

    // 도서 수정 폼 렌더링
    @GetMapping("/{bookId}/edit")
    public String showEditForm(@PathVariable Long bookId, Model model) {
        ResponseEntity<BookDetailDto> bookResponse = bookClient.getBookDetailsForAdmin(bookId);
        ResponseEntity<List<CategoryResponseDTO>> categoryResponse = categoryClient.getCategories();

        model.addAttribute("book", bookResponse.getBody());
        model.addAttribute("categories", categoryResponse.getBody());

        return "admin/book/book-edit";
    }


    // 도서 수정 처리
    @PostMapping(value = "/{bookId}/edit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String editBook(@PathVariable Long bookId,
                           @ModelAttribute BookRequestDto bookDto,
                           @RequestPart(value = "coverImage", required = false) MultipartFile coverImage,
                           @RequestPart(value = "additionalImages", required = false) List<MultipartFile> additionalImages,
                           @RequestParam(value = "deleteImages", required = false) List<Long> deleteImageIds) {
        bookClient.editBook(bookId, bookDto, coverImage, additionalImages, deleteImageIds);

        return "redirect:/books";
    }



    @GetMapping("/{bookId}")
    public String singleInquiryBookInfo(@PathVariable Long bookId, Model model) {
        BookDetailsDto book = bookClient.getBookDetails(bookId).getBody();
        model.addAttribute("bookInfo", book);

        return "bookdetail-test";
    }

    @GetMapping("/likes")
    public String getLikeBooks(@RequestParam(defaultValue = "1") String pageNum,
                               Model model, HttpServletRequest request) {
        String jwt = JwtUtil.getJwtFromCookie(request);
        if (jwt.equals("Bearer null")) {
            jwt = "";  // jwt가 null일 경우 빈 문자열 설정
        }
        ResponseEntity<Page<BookSearchResponseDTO>> likeBooks = bookClient.getLikeBooks(jwt, Long.parseLong(pageNum));
        int page = Integer.parseInt(pageNum) - 1; // 페이지 번호 0-based

        Page<BookSearchResponseDTO> books = likeBooks.getBody();
        model.addAttribute("books", books.getContent());
        model.addAttribute("currentPage", page+1);  // `Long` 타입
        model.addAttribute("totalPages", books.getTotalPages());  // `Integer` 타입
        return "book/book-likes";
    }

}
