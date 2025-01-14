package store.aurora.feign_client.book.tag;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import store.aurora.book.dto.tag.TagRequestDto;
import store.aurora.book.dto.tag.TagResponseDto;

import java.util.List;

@FeignClient(name = "tagClient", url = "${api.gateway.base-url}/api/tags")
public interface TagClient {

    @GetMapping("/search")
    ResponseEntity<List<TagResponseDto>> searchTags(@RequestParam String keyword);

    // 모든 태그 조회
    @GetMapping
    ResponseEntity<Page<TagResponseDto>> getTags(@RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "5") int size);
    // 태그 ID로 조회
//    @GetMapping("/{id}")
//    ResponseEntity<TagResponseDto> getTagById(@PathVariable("id") Long id);

    // 태그 생성
    @PostMapping
    ResponseEntity<TagResponseDto> createTag(@Valid @RequestBody TagRequestDto requestDto);

    // 태그 업데이트
    @PutMapping("/{id}")
    ResponseEntity<TagResponseDto> updateTag(@PathVariable("id") Long id, @Valid @RequestBody TagRequestDto requestDto);

    // 태그 삭제
    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteTag(@PathVariable("id") Long id);
}

