package store.aurora.feign_client.book;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestBody;
import store.aurora.book.dto.category.CategoryRequestDTO;
import store.aurora.book.dto.category.CategoryResponseDTO;

import java.util.List;

@FeignClient(name = "categoryClient", url = "${api.gateway.base-url}" + "/api/categories")
public interface CategoryClient {

    // 모든 카테고리 계층적으로 가져오기
    @GetMapping
    ResponseEntity<List<CategoryResponseDTO>> getCategories();

    @GetMapping("/root")
    ResponseEntity<Page<CategoryResponseDTO>> getRootCategories(@RequestParam(defaultValue = "0") int page,
                                                                @RequestParam(defaultValue = "5") int size);
    @GetMapping("/root/all")
    ResponseEntity<List<CategoryResponseDTO>> getAllRootCategories();

    @GetMapping("/{parent-id}/children")
    ResponseEntity<Page<CategoryResponseDTO>> getChildrenCategories(@PathVariable("parent-id") Long parentId,
                                                                    @RequestParam(defaultValue = "0") int page,
                                                                    @RequestParam(defaultValue = "5") int size);
    @GetMapping("/{parent-id}/children/all")
    ResponseEntity<List<CategoryResponseDTO>> getAllChildrenCategories(@PathVariable("parent-id") Long parentId);

    @PostMapping
    ResponseEntity<Void> createCategory(@RequestBody CategoryRequestDTO requestDTO);

    @PatchMapping("/{category-id}")
    ResponseEntity<Void> updateCategory(@PathVariable("category-id") Long categoryId,
                                            @RequestBody CategoryRequestDTO requestDTO);

    @DeleteMapping("/{category-id}")
    ResponseEntity<Void> deleteCategory(@PathVariable("category-id") Long categoryId);

    //search용 카테고리 기본값만 가져오기
    @GetMapping("/{category-id}")
    ResponseEntity<CategoryResponseDTO> findById(@PathVariable(value = "category-id") Long categoryId);

}

