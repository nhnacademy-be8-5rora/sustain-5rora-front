package store.aurora.feignClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import store.aurora.search.Page;
import store.aurora.search.dto.BookSearchResponseDTO;

@FeignClient(name = "bookSearchClient", url = "${api.gateway.base-url}" + "/api/books")
public interface BookSearchClient {

    @GetMapping("/search")
        // /api/books는 여기서 지정
    Page<BookSearchResponseDTO> searchBooksByKeyword(
            @RequestParam("type") String type,
            @RequestParam("keyword") String keyword,
            @RequestParam("pageNum") String pageNum
    );
}

