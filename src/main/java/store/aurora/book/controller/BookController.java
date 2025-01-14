package store.aurora.book.controller;

import feign.FeignException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import store.aurora.book.dto.BookDetailsDto;
import store.aurora.book.dto.aladin.AladinBookRequestDto;
import store.aurora.book.dto.aladin.BookDetailDto;
import store.aurora.book.dto.aladin.BookRequestDto;
import store.aurora.book.dto.aladin.BookResponseDto;
import store.aurora.book.dto.category.CategoryResponseDTO;
import store.aurora.book.util.PaginationUtil;
import store.aurora.common.JwtUtil;
import store.aurora.feign_client.book.AladinBookClient;
import store.aurora.feign_client.book.BookClient;
import store.aurora.feign_client.book.CategoryClient;
import store.aurora.search.dto.BookSearchResponseDTO;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {
    private static final String BOOKS_ATTRIBUTE = "books";
    private static final String CATEGORIES_ATTRIBUTE = "categories";
    private static final String REDIRECT_BOOKS = "redirect:/books";
    private static final String ALADIN_REGISTER_VIEW = "admin/book/aladin/register";
    private static final String REGISTER_VIEW = "admin/book/register";
    private static final String BOOK_EDIT_VIEW = "admin/book/book-edit";


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
        ResponseEntity<List<AladinBookRequestDto>> response = aladinBookClient.searchBooks(query, queryType, searchTarget, start);
        List<AladinBookRequestDto> books = response.getBody(); // ResponseEntity에서 Body 추출
        model.addAttribute(BOOKS_ATTRIBUTE, books); // 검색 결과
        model.addAttribute("currentPage", start); // 현재 페이지
        model.addAttribute("query", query); // 검색어
        model.addAttribute("queryType", queryType); // 검색 유형
        model.addAttribute("searchTarget", searchTarget); // 검색 대상
        return "admin/book/aladin/search";
    }

    // API 도서 등록 폼 렌더링
    @GetMapping("/aladin/register")
    public String showRegisterForm(@RequestParam String isbn13, Model model) {

        ResponseEntity<AladinBookRequestDto> response = aladinBookClient.getBookDetailsByIsbn(isbn13);
        ResponseEntity<List<CategoryResponseDTO>> responseCategory = categoryClient.getCategories();
        model.addAttribute("book", response.getBody());
        model.addAttribute(CATEGORIES_ATTRIBUTE, responseCategory.getBody());
        return ALADIN_REGISTER_VIEW;
    }

    // API 도서 등록 처리
    @PostMapping(value = "/aladin/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String registerApiBook(@Valid @ModelAttribute("book") AladinBookRequestDto bookDto,
                                  BindingResult bindingResult,
                                  @RequestPart(value = "additionalImages", required = false) List<MultipartFile> additionalImages,
                                  Model model) {
        model.addAttribute(CATEGORIES_ATTRIBUTE, categoryClient.getCategories().getBody());
        if (bindingResult.hasErrors()) {
            return ALADIN_REGISTER_VIEW;
        }
        try {
            aladinBookClient.registerApiBook(bookDto,additionalImages);
        } catch (FeignException ex) {
            model.addAttribute("backendErrors", ex.contentUTF8());
            return ALADIN_REGISTER_VIEW;
        }
        return REDIRECT_BOOKS;
    }

    // 직접 등록 폼 렌더링
    @GetMapping("/register")
    public String showDirectRegisterForm(Model model) {
        ResponseEntity<List<CategoryResponseDTO>> responseCategory = categoryClient.getCategories();
        model.addAttribute("book", new BookRequestDto()); // 빈 객체 전달
        model.addAttribute(CATEGORIES_ATTRIBUTE, responseCategory.getBody());
        return REGISTER_VIEW;
    }

    // 직접 도서 등록 처리
    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String registerDirectBook(
            @Valid @ModelAttribute("book") BookRequestDto bookDto,
            BindingResult bindingResult,
            @RequestPart(value = "coverImage", required = false) MultipartFile coverImage,
            @RequestPart(value = "additionalImages", required = false) List<MultipartFile> additionalImages,
            Model model) {
        model.addAttribute(CATEGORIES_ATTRIBUTE, categoryClient.getCategories().getBody());
        if (bindingResult.hasErrors()) {
            return REGISTER_VIEW; // 유효성 검증 실패 시 다시 폼 렌더링
        }
        try {
            bookClient.registerDirectBook(bookDto, coverImage, additionalImages);
        } catch (FeignException ex) {
            // FeignException 발생 시 처리
            model.addAttribute("backendErrors", ex.contentUTF8()); // 백엔드에서 반환된 에러 메시지 처리
            return REGISTER_VIEW; // 에러 발생 시 다시 폼 렌더링
        }
        return REDIRECT_BOOKS;
    }

    @GetMapping
    public String listBooks(@RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "5") int size,
                            Model model) {
        ResponseEntity<Page<BookResponseDto>> response = bookClient.getAllBooks(page, size);
        Page<BookResponseDto> bookPage = Optional.ofNullable(response.getBody()).orElse(Page.empty());

        PaginationUtil.addPaginationAttributes(model, bookPage, BOOKS_ATTRIBUTE, 5);
        return "admin/book/book-list";
    }

    // 도서 수정 폼 렌더링
    @GetMapping("/{bookId}/edit")
    public String showEditForm(@PathVariable Long bookId, Model model) {
        ResponseEntity<BookDetailDto> bookResponse = bookClient.getBookDetailsForAdmin(bookId);
        ResponseEntity<List<CategoryResponseDTO>> categoryResponse = categoryClient.getCategories();

        model.addAttribute("book", bookResponse.getBody());
        model.addAttribute("bookId", bookId); // 책 ID를 별도로 전달
        model.addAttribute(CATEGORIES_ATTRIBUTE, categoryResponse.getBody());

        return BOOK_EDIT_VIEW;
    }

    // 도서 수정 처리
    @PostMapping(value = "/{bookId}/edit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String editBook(@PathVariable Long bookId,
                           @Valid @ModelAttribute("book") BookRequestDto bookDto,
                           BindingResult bindingResult,
                           @RequestPart(value = "coverImage", required = false) MultipartFile coverImage,
                           @RequestPart(value = "additionalImages", required = false) List<MultipartFile> additionalImages,
                           @RequestParam(value = "deleteImages", required = false) List<Long> deleteImageIds,
                           Model model) {
        model.addAttribute(CATEGORIES_ATTRIBUTE, categoryClient.getCategories().getBody());
        model.addAttribute("bookId", bookId); // 책 ID를 다시 모델에 추가

        if (bindingResult.hasErrors()) {
            System.out.println("Validation errors: " + bindingResult.getAllErrors());
            return BOOK_EDIT_VIEW;
        }

        try {
            bookClient.editBook(bookId, bookDto, coverImage, additionalImages, deleteImageIds);
        } catch (FeignException ex) {
            model.addAttribute("backendErrors", ex.contentUTF8()); // 백엔드에서 반환된 에러 메시지 처리
            return BOOK_EDIT_VIEW;
        }
        return REDIRECT_BOOKS;
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
        model.addAttribute(BOOKS_ATTRIBUTE, books.getContent());
        model.addAttribute("currentPage", page+1);  // `Long` 타입
        model.addAttribute("totalPages", books.getTotalPages());  // `Integer` 타입
        return "book/book-likes";
    }

}
