package store.aurora.book.controller.category;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import store.aurora.book.dto.category.CategoryResponseDTO;
import store.aurora.feign_client.book.CategoryClient;

import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryClient categoryClient;

    @GetMapping("/root")
    public ResponseEntity<List<CategoryResponseDTO>> getCategories() {
        return ResponseEntity.ok(categoryClient.getCategories().getBody());
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<List<CategoryResponseDTO>> getCategory(@PathVariable("categoryId") Long categoryId) {
        if (categoryId == null) {
            categoryId = 0L;
        }
        ResponseEntity<CategoryResponseDTO> categories = categoryClient.findById(categoryId);
        if (categories.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)  // JSON 형식으로 설정
                    .body(Objects.requireNonNull(categories.getBody()).getChildren());
        }
        return ResponseEntity.badRequest().build();
    }

    // 특정 부모 ID를 가진 자식 카테고리 목록 가져오기
    @GetMapping("/{parentId}/children")
    public ResponseEntity<List<CategoryResponseDTO>> getChildrenCategories(@PathVariable Long parentId) {
        ResponseEntity<List<CategoryResponseDTO>> response = categoryClient.getAllChildrenCategories(parentId);
        return ResponseEntity.ok(response.getBody());
    }
}
