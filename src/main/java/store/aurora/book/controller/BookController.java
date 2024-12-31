package store.aurora.book.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import store.aurora.book.dto.BookDetailsDto;
import store.aurora.book.dto.aladin.BookDetailDto;
import store.aurora.book.dto.aladin.BookRequestDto;
import store.aurora.book.dto.aladin.BookResponseDto;
import store.aurora.book.dto.category.CategoryResponseDTO;
import store.aurora.book.dto.tag.TagResponseDto;
import store.aurora.feignClient.book.BookClient;
import store.aurora.feignClient.book.CategoryClient;
import store.aurora.feignClient.book.tag.TagClient;

import java.util.List;

@Controller
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {

    private final BookClient bookClient;
    private final CategoryClient categoryClient;
    private final TagClient tagClient;

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
        ResponseEntity<List<BookRequestDto>> response = bookClient.searchBooks(query, queryType, searchTarget, start);
        List<BookRequestDto> books = response.getBody(); // ResponseEntity에서 Body 추출
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
        ResponseEntity<BookRequestDto> response = bookClient.getBookById(bookId);
        ResponseEntity<List<CategoryResponseDTO>> responseCategory = categoryClient.getCategoryHierarchy();
        ResponseEntity<List<TagResponseDto>> tagResponse = tagClient.getAllTags();
        model.addAttribute("categories", responseCategory.getBody());
        model.addAttribute("tags", tagResponse.getBody());
        model.addAttribute("book", response.getBody());
        return "/admin/book/api/register";
    }

    // API 도서 등록 처리
    @PostMapping(value = "/api/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String registerApiBook(@ModelAttribute BookRequestDto bookDto,
                                  @RequestPart(value = "uploadedImages", required = false) List<MultipartFile> additionalImages
    ) {
        bookClient.registerApiBook(bookDto,additionalImages);
        return "redirect:/books/list";
    }

    // 직접 등록 폼 렌더링
    @GetMapping("/direct/register")
    public String showDirectRegisterForm(Model model) {
        ResponseEntity<List<CategoryResponseDTO>> responseCategory = categoryClient.getCategoryHierarchy();
        ResponseEntity<List<TagResponseDto>> tagResponse = tagClient.getAllTags();
        model.addAttribute("categories", responseCategory.getBody());
        model.addAttribute("tags", tagResponse.getBody());
        model.addAttribute("book", new BookRequestDto()); // 빈 객체 전달
        return "/admin/book/direct-register"; // 직접 등록 페이지 템플릿 경로
    }

    // 직접 도서 등록 처리
    @PostMapping(value = "/direct/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String registerDirectBook(
            @ModelAttribute BookRequestDto bookDto,
            @RequestPart(value = "coverImage", required = false) MultipartFile coverImage,
            @RequestParam(value = "additionalImages", required = false) List<MultipartFile> additionalImages

    ) {
        // Feign 클라이언트를 통해 데이터 전달
        bookClient.registerDirectBook(bookDto, coverImage,additionalImages);
        return "redirect:/books/list";
    }

    // 도서 목록 페이지 렌더링
    @GetMapping("/list")
    public String listBooks(Model model) {
        ResponseEntity<List<BookResponseDto>> response = bookClient.getAllBooks();
        List<BookResponseDto> books = response.getBody();
        model.addAttribute("books", books);
        return "/admin/book/book-list"; // 도서 목록 페이지 템플릿
    }

    // 도서 수정 폼 렌더링
    @GetMapping("/{bookId}/edit")
    public String showEditForm(@PathVariable Long bookId, Model model) {
        // 기존 도서 정보 가져오기
        ResponseEntity<BookDetailDto> bookResponse = bookClient.getBookDetailsForAdmin(bookId);

        // 카테고리 및 태그 정보 가져오기
        ResponseEntity<List<CategoryResponseDTO>> categoryResponse = categoryClient.getCategoryHierarchy();
        ResponseEntity<List<TagResponseDto>> tagResponse = tagClient.getAllTags();

        // 모델에 데이터 추가
        model.addAttribute("book", bookResponse.getBody());
        model.addAttribute("categories", categoryResponse.getBody());
        model.addAttribute("tags", tagResponse.getBody());

        return "/admin/book/book-edit"; // 수정 페이지 템플릿 경로
    }


    // 도서 수정 처리
    @PostMapping(value = "/{bookId}/edit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String editBook(@PathVariable Long bookId,
                           @ModelAttribute BookRequestDto bookDto,
                           @RequestPart(value = "coverImage", required = false) MultipartFile coverImage,
                           @RequestPart(value = "additionalImages", required = false) List<MultipartFile> additionalImages,
                           @RequestPart(value = "deleteImages", required = false) List<Long> deleteImageIds) {
        // Feign 클라이언트를 통해 수정 요청 전달
        bookClient.editBook(bookId, bookDto, coverImage, additionalImages, deleteImageIds);

        return "redirect:/books/list"; // 수정 후 목록 페이지로 리다이렉트
    }



    @GetMapping("/{bookId}")
    public String singleInquiryBookInfo(@PathVariable Long bookId, Model model) {
        BookDetailsDto book = bookClient.getBookDetails(bookId).getBody();
        model.addAttribute("bookInfo", book);

        return "bookdetail-test";
    }
}
