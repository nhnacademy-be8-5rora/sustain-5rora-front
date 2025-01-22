package store.aurora.feign_client.book;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import store.aurora.book.dto.*;
import store.aurora.book.dto.aladin.*;
import store.aurora.config.security.constants.SecurityConstants;
import store.aurora.search.dto.BookSearchResponseDTO;

import java.util.List;

@FeignClient(name = "bookClient", url = "${api.gateway.base-url}" + "/api/books")
public interface BookClient {

    // 직접 도서 등록
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<Void> createBook(@SpringQueryMap BookRequestDto bookDto,
                                            @RequestPart(value = "coverImage", required = false) MultipartFile coverImage,
                                            @RequestPart(value = "additionalImages", required = false) List<MultipartFile> additionalImages
    );
    
    // API 도서 등록
    @PostMapping(value = "/aladin", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<Void> createApiBook(@RequestPart("book") AladinBookRequestDto bookDto,
                                         @RequestPart(value = "additionalImages", required = false) List<MultipartFile> additionalImages
    );
    @GetMapping
    ResponseEntity<Page<BookResponseDto>> getAllBooks(@RequestParam(defaultValue = "0") int page,
                                                      @RequestParam(defaultValue = "5") int size);

    @PutMapping(value = "/{book-id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    void editBook(@PathVariable("book-id") Long bookId,
                  @SpringQueryMap BookDetailDto bookDto,
                  @RequestPart(value = "coverImage", required = false) MultipartFile coverImage,
                  @RequestPart(value = "additionalImages", required = false) List<MultipartFile> additionalImages,
                  @RequestParam(value = "deleteImages", required = false) List<Long> deleteImageIds);

    @GetMapping("/{book-id}/edit")
    ResponseEntity<BookDetailDto> getBookDetailsForAdmin(@PathVariable("book-id") Long bookId);

    @GetMapping("/{book-id}")
    ResponseEntity<BookDetailsDto> getBookDetails(@PathVariable("book-id") Long bookId);

    // 도서 상세 정보 조회
    @GetMapping("/aladin/{book-id}")
    ResponseEntity<BookDetailDto> getBookDetailsById(@PathVariable("book-id") Long bookId);

    @GetMapping("/deactivate")
    ResponseEntity<Page<BookResponseDto>> getDeactivateBooks(@RequestParam(defaultValue = "0") int page,
                                                             @RequestParam(defaultValue = "5") int size);

    @PostMapping("/{book-id}/deactivate")
    ResponseEntity<Void> deactivateBook(@PathVariable("book-id") Long bookId);

    @PostMapping("/{book-id}/activate")
    ResponseEntity<Void> activateBook(@PathVariable("book-id") Long bookId);

    @GetMapping("/my-likeBooks")
    ResponseEntity<Page<BookSearchResponseDTO>> getLikedBooksByUser(@RequestHeader(value = SecurityConstants.AUTHORIZATION_HEADER, required = false) String jwtToken,
                                                             @RequestParam Long pageNum);

    @GetMapping("/most-sold")
    ResponseEntity<BookSearchResponseDTO> findMostSoldByLastMonth();

}