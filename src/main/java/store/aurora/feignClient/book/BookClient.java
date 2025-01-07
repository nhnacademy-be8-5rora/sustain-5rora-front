package store.aurora.feignClient.book;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import store.aurora.book.dto.*;
import store.aurora.book.dto.aladin.BookDetailDto;
import store.aurora.book.dto.aladin.BookRequestDto;
import store.aurora.book.dto.aladin.BookResponseDto;
import store.aurora.config.security.constants.SecurityConstants;
import store.aurora.search.dto.BookSearchResponseDTO;

import java.util.List;

@FeignClient(name = "bookClient", url = "${api.gateway.base-url}" + "/api/books")
public interface BookClient {

    // 도서 검색 API
    @GetMapping("/aladin/search")
    ResponseEntity<List<BookRequestDto>> searchBooks(@RequestParam("query") String query,
                                              @RequestParam("queryType") String queryType,
                                              @RequestParam("searchTarget") String searchTarget,
                                              @RequestParam(value = "start", defaultValue = "1") int start);

    // 특정 도서 정보 가져오기
    @GetMapping("/aladin/{bookId}")
    ResponseEntity<BookRequestDto> getBookById(@PathVariable("bookId") String bookId);

    // API 도서 등록
    @PostMapping(value = "/aladin/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<Void> registerApiBook(@RequestPart("book") BookRequestDto bookDto,
                                         @RequestPart(value = "additionalImages", required = false) List<MultipartFile> additionalImages
    );

    // 직접 도서 등록
    @PostMapping(value = "/direct/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<Void> registerDirectBook(@RequestPart("book") BookRequestDto bookDto,
                                            @RequestPart(value = "coverImage", required = false) MultipartFile coverImage,
                                            @RequestPart(value = "additionalImages", required = false) List<MultipartFile> additionalImages
    );

    @GetMapping
    ResponseEntity<Page<BookResponseDto>> getAllBooks(@RequestParam(defaultValue = "0") int page,
                                                      @RequestParam(defaultValue = "2") int size);

    @PutMapping(value = "/{bookId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    void editBook(@PathVariable Long bookId,
                  @RequestPart("book") BookRequestDto bookDto,
                  @RequestPart(value = "coverImage", required = false) MultipartFile coverImage,
                  @RequestPart(value = "additionalImages", required = false) List<MultipartFile> additionalImages,
                  @RequestPart(value = "deleteImages", required = false) List<Long> deleteImageIds);

    @GetMapping("/{bookId}/edit")
    ResponseEntity<BookDetailDto> getBookDetailsForAdmin(@PathVariable Long bookId);

    @GetMapping("/{bookId}")
    ResponseEntity<BookDetailsDto> getBookDetails(@PathVariable Long bookId);

        // 도서 상세 정보 조회
    @GetMapping("/aladin/{bookId}")
    ResponseEntity<BookDetailDto> getBookDetailsById(@PathVariable("bookId") Long bookId);




    // 책 삭제
    @DeleteMapping("/{bookId}")
    ResponseEntity<Void> deleteBook(@PathVariable("bookId") Long bookId);

    @GetMapping("/likes")
    ResponseEntity<Page<BookSearchResponseDTO>> getLikeBooks(@RequestHeader(value = SecurityConstants.AUTHORIZATION_HEADER, required = false) String jwtToken,
                                                             @RequestParam Long pageNum);

    @GetMapping("/most")
    ResponseEntity<BookSearchResponseDTO> getMostBook();

}