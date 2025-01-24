package store.aurora.feign_client.search;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import store.aurora.config.security.constants.SecurityConstants;
import store.aurora.search.dto.BookSearchResponseDTO;

@FeignClient(name = "elasticSearchClient", url = "${api.gateway.base-url}/api/books/search")
public interface ElasticSearchClient {

    @GetMapping("/elastic-search")
    Page<BookSearchResponseDTO> searchBooks(@RequestHeader(value = SecurityConstants.AUTHORIZATION_HEADER, required = false) String jwtToken,
                                                     @RequestParam("type") String type,
                                            @RequestParam("keyword") String keyword,
                                            @RequestParam(required = false,defaultValue = "1") int pageNum
    );

}
