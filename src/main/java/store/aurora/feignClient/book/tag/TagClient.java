package store.aurora.feignClient.book.tag;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import store.aurora.book.dto.tag.BookTagRequestDto;
import store.aurora.book.dto.tag.TagRequestDto;

@FeignClient(name = "tagClient", url = "${api.gateway.base-url}/api/tags")
public interface TagClient {

    // 태그 생성
    @PostMapping
    ResponseEntity<Void> createTag(@RequestBody TagRequestDto requestDto);

    // 태그 삭제
    @DeleteMapping("/{tagId}")
    ResponseEntity<Void> removeTag(@PathVariable(value = "tagId") Long tagId);

    // 책에 태그 추가
    @PostMapping("/book-tag")
    ResponseEntity<Void> addBookTag(@RequestBody BookTagRequestDto requestDto);

    // 책의 태그 삭제
    @DeleteMapping("/book-tag/{bookTagId}")
    ResponseEntity<Void> removeBookTag(@PathVariable(value = "bookTagId") Long bookTagId);
}

