package store.aurora.feign_client.book.author;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import store.aurora.book.dto.author.AuthorRequestDto;
import store.aurora.book.dto.author.AuthorResponseDto;

@FeignClient(name = "authorClient", url = "${api.gateway.base-url}" + "/api/authors")
public interface AuthorClient {

    @GetMapping
    ResponseEntity<Page<AuthorResponseDto>> getAllAuthors(@RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "5") int size);

    @GetMapping("/{id}")
    ResponseEntity<AuthorResponseDto> getAuthorById(@PathVariable("id") Long id);

    @PostMapping
    ResponseEntity<Void> createAuthor(@RequestBody AuthorRequestDto requestDto);

    @PutMapping("/{id}")
    ResponseEntity<Void> updateAuthor(@PathVariable("id") Long id, @RequestBody AuthorRequestDto requestDto);

    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteAuthor(@PathVariable("id") Long id);
}