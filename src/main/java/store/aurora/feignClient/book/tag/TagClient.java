package store.aurora.feignClient.book.tag;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import store.aurora.book.dto.tag.BookTagRequestDto;
import store.aurora.book.dto.tag.TagRequestDto;
import store.aurora.book.dto.tag.TagResponseDto;

import java.util.List;

@FeignClient(name = "tagClient", url = "${api.gateway.base-url}")
public interface TagClient {

    @GetMapping("/api/tags")
    ResponseEntity<List<TagResponseDto>> getAllTags();
    // 태그 생성
    @PostMapping("/api/tags")
    ResponseEntity<Void> createTag(@RequestBody TagRequestDto requestDto);

    // 태그 삭제
    @DeleteMapping("/api/tags/{tagId}")
    ResponseEntity<Void> removeTag(@PathVariable(value = "tagId") Long tagId);

    // 책에 태그 추가
    @PostMapping("/book-tag")
    ResponseEntity<Void> addBookTag(@RequestBody BookTagRequestDto requestDto);

    // 책의 태그 삭제
    @DeleteMapping("/book-tag/{bookTagId}")
    ResponseEntity<Void> removeBookTag(@PathVariable(value = "bookTagId") Long bookTagId);
}

