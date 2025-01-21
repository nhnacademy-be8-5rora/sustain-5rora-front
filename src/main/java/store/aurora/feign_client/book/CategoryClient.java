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

    @GetMapping("/{parentId}/children")
    ResponseEntity<Page<CategoryResponseDTO>> getChildrenCategories(@PathVariable Long parentId,
                                                                    @RequestParam(defaultValue = "0") int page,
                                                                    @RequestParam(defaultValue = "5") int size);
    @GetMapping("/{parentId}/children/all")
    ResponseEntity<List<CategoryResponseDTO>> getAllChildrenCategories(@PathVariable Long parentId);
    @PostMapping
    ResponseEntity<Void> createCategory(@RequestBody CategoryRequestDTO requestDTO);

    @PatchMapping("/{categoryId}")
    ResponseEntity<Void> updateCategoryName(@PathVariable("categoryId") Long categoryId,
                                            @RequestBody CategoryRequestDTO requestDTO);

    @DeleteMapping("/{categoryId}")
    ResponseEntity<Void> deleteCategory(@PathVariable("categoryId") Long categoryId);

    //search용 카테고리 기본값만 가져오기
    @GetMapping("/{categoryId}")
    ResponseEntity<CategoryResponseDTO> findById(@PathVariable(value = "categoryId") Long categoryId);

}

