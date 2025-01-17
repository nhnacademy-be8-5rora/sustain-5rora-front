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
    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<Void> registerDirectBook(@SpringQueryMap BookRequestDto bookDto,
                                            @RequestPart(value = "coverImage", required = false) MultipartFile coverImage,
                                            @RequestPart(value = "additionalImages", required = false) List<MultipartFile> additionalImages
    );

    @GetMapping
    ResponseEntity<Page<BookResponseDto>> getAllBooks(@RequestParam(defaultValue = "0") int page,
                                                      @RequestParam(defaultValue = "5") int size);

    @PutMapping(value = "/{bookId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    void editBook(@PathVariable Long bookId,
                  @SpringQueryMap BookDetailDto bookDto,
                  @RequestPart(value = "coverImage", required = false) MultipartFile coverImage,
                  @RequestPart(value = "additionalImages", required = false) List<MultipartFile> additionalImages,
                  @RequestParam(value = "deleteImages", required = false) List<Long> deleteImageIds);

    @GetMapping("/{bookId}/edit")
    ResponseEntity<BookDetailDto> getBookDetailsForAdmin(@PathVariable Long bookId);

    @GetMapping("/{bookId}")
    ResponseEntity<BookDetailsDto> getBookDetails(@PathVariable Long bookId);

    // 도서 상세 정보 조회
    @GetMapping("/aladin/{bookId}")
    ResponseEntity<BookDetailDto> getBookDetailsById(@PathVariable("bookId") Long bookId);

    @GetMapping("/deactivate")
    ResponseEntity<Page<BookResponseDto>> getDeactivateBooks(@RequestParam(defaultValue = "0") int page,
                                                             @RequestParam(defaultValue = "5") int size);

    @PostMapping("/{bookId}/deactivate")
    ResponseEntity<Void> deactivateBook(@PathVariable Long bookId);

    @PostMapping("/{bookId}/activate")
    ResponseEntity<Void> activateBook(@PathVariable Long bookId);

    @GetMapping("/likes")
    ResponseEntity<Page<BookSearchResponseDTO>> getLikeBooks(@RequestHeader(value = SecurityConstants.AUTHORIZATION_HEADER, required = false) String jwtToken,
                                                             @RequestParam Long pageNum);

    @GetMapping("/most")
    ResponseEntity<BookSearchResponseDTO> getMostBook();

}