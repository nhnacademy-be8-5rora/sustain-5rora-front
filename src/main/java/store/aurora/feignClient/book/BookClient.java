package store.aurora.feignClient.book;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import store.aurora.book.dto.BookDetailsDto;

@FeignClient(name = "bookClient", url = "${api.gateway.base-url}" + "/api/books")
public interface BookClient {

    @GetMapping("/{bookId}")
    ResponseEntity<BookDetailsDto> getBookDetails(@PathVariable(value = "bookId") Long bookId);
}
