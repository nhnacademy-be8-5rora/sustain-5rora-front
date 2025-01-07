package store.aurora.feignClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import store.aurora.config.security.constants.SecurityConstants;
import store.aurora.search.dto.BookSearchResponseDTO;
import org.springframework.data.domain.Page;

@FeignClient(name = "bookSearchClient", url = "${api.gateway.base-url}/api/books")
public interface BookSearchClient {

    @GetMapping("/search")
    Page<BookSearchResponseDTO> searchBooksByKeyword(@RequestHeader(value = SecurityConstants.AUTHORIZATION_HEADER, required = false) String jwtToken,
                                                     @RequestParam("type") String type,
            @RequestParam("keyword") String keyword,
            @RequestParam("pageNum") String pageNum,
            @RequestParam(required = false) String orderBy,
            @RequestParam(required = false) String orderDirection
    );
}
