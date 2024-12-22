package store.aurora.feignClient.book;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import store.aurora.book.dto.*;

import java.util.List;

@FeignClient(name = "bookClient", url = "${api.gateway.base-url}" + "/api/books")
public interface BookClient {

    // 책 등록
    @PostMapping
    ResponseEntity<Void> createBook(@RequestBody BookRequestDTO requestDTO);

    // 책 세부 정보 업데이트
    @PutMapping("/{bookId}/details")
    ResponseEntity<Void> updateBookDetails(@PathVariable("bookId") Long bookId,
                                           @RequestBody BookDetailsUpdateDTO updateDTO);

    // 책 판매 정보 업데이트
    @PutMapping("/{bookId}/sales-info")
    ResponseEntity<Void> updateBookSalesInfo(@PathVariable("bookId") Long bookId,
                                             @RequestBody BookSalesInfoUpdateDTO salesInfoDTO);

    // 책 포장 상태 업데이트
    @PatchMapping("/{bookId}/packaging")
    ResponseEntity<Void> updateBookPackaging(@PathVariable("bookId") Long bookId,
                                             @RequestParam("packaging") boolean packaging);

    // 책 세부 정보 조회
    @GetMapping("/{bookId}")
    ResponseEntity<BookDetailsDto> getBookDetails(@PathVariable("bookId") Long bookId);

    // 모든 책 조회 (페이지네이션)
    @GetMapping
    ResponseEntity<List<BookResponseDTO>> getBooks(@RequestParam(value = "page", defaultValue = "0") int page,
                                                   @RequestParam(value = "size", defaultValue = "10") int size);

    // 책 삭제
    @DeleteMapping("/{bookId}")
    ResponseEntity<Void> deleteBook(@PathVariable("bookId") Long bookId);
}