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

    // 모든 태그 조회
    @GetMapping
    ResponseEntity<Page<TagResponseDto>> getTags(@RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "5") int size);
    // 태그 ID로 조회
    @GetMapping("/{tag-id}")
    ResponseEntity<TagResponseDto> getTagById(@PathVariable("tag-id") Long id);

    // 태그 생성
    @PostMapping
    ResponseEntity<TagResponseDto> createTag(@Valid @RequestBody TagRequestDto requestDto);

    // 태그 업데이트
    @PutMapping("/{tag-id}")
    ResponseEntity<TagResponseDto> updateTag(@PathVariable("tag-id") Long id, @Valid @RequestBody TagRequestDto requestDto);

    // 태그 삭제
    @DeleteMapping("/{tag-id}")
    ResponseEntity<Void> deleteTag(@PathVariable("tag-id") Long id);
}

