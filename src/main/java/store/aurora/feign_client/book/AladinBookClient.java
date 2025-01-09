package store.aurora.feign_client.book;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import store.aurora.book.dto.aladin.AladinBookDto;
import store.aurora.book.dto.aladin.BookRequestDto;

import java.util.List;

@FeignClient(name = "aladinBookClient", url = "${api.gateway.base-url}" + "/api/aladin")
public interface AladinBookClient {

    // 도서 검색 API
    @GetMapping("/search")
    ResponseEntity<List<AladinBookDto>> searchBooks(@RequestParam String query,
                                                    @RequestParam String queryType,
                                                    @RequestParam String searchTarget,
                                                    @RequestParam int start);

    // 특정 도서 정보 가져오기
    @GetMapping("/{isbn13}")
    public ResponseEntity<AladinBookDto> getBookDetailsByIsbn(@PathVariable String isbn13);

    // API 도서 등록
    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<Void> registerApiBook(@RequestPart("book") BookRequestDto bookDto,
                                         @RequestPart(value = "additionalImages", required = false) List<MultipartFile> additionalImages
    );

}