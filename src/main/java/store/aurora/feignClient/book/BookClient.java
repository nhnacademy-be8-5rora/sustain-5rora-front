package store.aurora.feignClient.book;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import store.aurora.book.dto.*;
import store.aurora.book.dto.aladin.BookDetailDto;
import store.aurora.book.dto.aladin.BookDto;
import store.aurora.book.dto.aladin.BookRequestDtoEx;

import java.awt.print.Book;
import java.util.List;

@FeignClient(name = "bookClient", url = "${api.gateway.base-url}" + "/api/books")
public interface BookClient {

    // 도서 검색 API
    @GetMapping("/aladin/search")
    ResponseEntity<List<BookDto>> searchBooks(@RequestParam("query") String query,
                                              @RequestParam("queryType") String queryType,
                                              @RequestParam("searchTarget") String searchTarget,
                                              @RequestParam(value = "start", defaultValue = "1") int start);

    // 특정 도서 정보 가져오기
    @GetMapping("/aladin/{bookId}")
    ResponseEntity<BookDto> getBookById(@PathVariable("bookId") String bookId);

    // API 도서 등록
    @PostMapping("/aladin/register")
    ResponseEntity<Void> registerApiBook(@ModelAttribute BookRequestDtoEx bookRequestDto);

    // 직접 도서 등록
    @PostMapping("/register/direct")
    ResponseEntity<Void> registerDirectBook(@ModelAttribute BookRequestDtoEx bookRequestDto);
    // 도서 상세 정보 조회
    @GetMapping("/aladin/{bookId}")
    ResponseEntity<BookDetailDto> getBookDetailsById(@PathVariable("bookId") Long bookId);



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


    // 책 삭제
    @DeleteMapping("/{bookId}")
    ResponseEntity<Void> deleteBook(@PathVariable("bookId") Long bookId);
}