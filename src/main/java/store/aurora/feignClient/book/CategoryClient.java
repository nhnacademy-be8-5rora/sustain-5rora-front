package store.aurora.feignClient.book;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import store.aurora.book.dto.BookDetailsDto;
import store.aurora.book.dto.category.CategoryDTO;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestBody;
import store.aurora.book.dto.category.CategoryRequestDTO;
import store.aurora.book.dto.category.CategoryResponseDTO;

import java.util.List;

@FeignClient(name = "categoryClient", url = "${api.gateway.base-url}" + "/api/categories")
public interface CategoryClient {

    @GetMapping("/{categoryId}")
    ResponseEntity<List<CategoryDTO>> findCategoriesByParentId(@PathVariable(value = "categoryId") Long categoryId);

    @PostMapping
    ResponseEntity<Void> createCategory(@RequestBody CategoryRequestDTO requestDTO);

    @PatchMapping("/{categoryId}")
    ResponseEntity<Void> updateCategoryName(@PathVariable("categoryId") Long categoryId,
                                            @RequestBody CategoryRequestDTO requestDTO);

    @DeleteMapping("/{categoryId}")
    ResponseEntity<Void> deleteCategory(@PathVariable("categoryId") Long categoryId);

    @GetMapping
    ResponseEntity<List<CategoryResponseDTO>> getAllCategories();

    // 카테고리 계층형 데이터 가져오기
    @GetMapping("/hierarchy")
    ResponseEntity<List<CategoryResponseDTO>> getCategoryHierarchy();

    // 특정 카테고리 가져오기
    @GetMapping("/{categoryId}")
    ResponseEntity<CategoryResponseDTO> getCategoryById(@PathVariable("categoryId") Long categoryId);
@GetMapping("/{categoryId}")
ResponseEntity<CategoryDTO> findById(@PathVariable(value = "categoryId") Long categoryId);
}

