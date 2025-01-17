package store.aurora.feign_client.book.publisher;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import store.aurora.book.dto.publisher.PublisherRequestDto;
import store.aurora.book.dto.publisher.PublisherResponseDto;


@FeignClient(name = "publisherClient", url = "${api.gateway.base-url}" + "/api/publishers")
public interface PublisherClient {

    @GetMapping
    ResponseEntity<Page<PublisherResponseDto>> getAllPublishers(@RequestParam(defaultValue = "0") int page,
                                                                @RequestParam(defaultValue = "5") int size);

    @GetMapping("/{id}")
    ResponseEntity<PublisherResponseDto> getPublisherById(@PathVariable("id") Long id);

    @PostMapping
    ResponseEntity<Void> createPublisher(@RequestBody PublisherRequestDto requestDto);

    @PutMapping("/{id}")
    ResponseEntity<Void> updatePublisher(@PathVariable("id") Long id, @RequestBody PublisherRequestDto requestDto);

    @DeleteMapping("/{id}")
    ResponseEntity<Void> deletePublisher(@PathVariable("id") Long id);
}
