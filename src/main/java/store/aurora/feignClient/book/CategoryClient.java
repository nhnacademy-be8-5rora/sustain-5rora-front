package store.aurora.feignClient.book;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @PostMapping
    ResponseEntity<Void> createCategory(@RequestBody CategoryRequestDTO requestDTO);

    @PatchMapping("/{categoryId}")
    ResponseEntity<Void> updateCategoryName(@PathVariable("categoryId") Long categoryId,
                                            @RequestBody CategoryRequestDTO requestDTO);

    @DeleteMapping("/{categoryId}")
    ResponseEntity<Void> deleteCategory(@PathVariable("categoryId") Long categoryId);

    @GetMapping("all")
    ResponseEntity<List<CategoryResponseDTO>> getAllCategories();

    @GetMapping("paged")
    ResponseEntity<Page<CategoryResponseDTO>> getPagedCategories(@RequestParam(defaultValue = "0") int page,
                                                                 @RequestParam(defaultValue = "2") int size);
    // 카테고리 계층형 데이터 가져오기
    @GetMapping("/hierarchy")
    ResponseEntity<List<CategoryResponseDTO>> getCategoryHierarchy();

    // 특정 카테고리 가져오기
//    @GetMapping("/{categoryId}")
//    ResponseEntity<CategoryResponseDTO> getCategoryById(@PathVariable("categoryId") Long categoryId);


    //search용 카테고리 기본값만 가져오기
    @GetMapping("/{categoryId}")
    ResponseEntity<CategoryDTO> findById(@PathVariable(value = "categoryId") Long categoryId);

    @GetMapping("/root/paged")
    ResponseEntity<Page<CategoryResponseDTO>> getRootCategories(@RequestParam(defaultValue = "0") int page,
                                                                @RequestParam(defaultValue = "2") int size);

    /**
     * 특정 카테고리의 직속 하위 카테고리 가져오기
     */
    @GetMapping("/{parentId}/children/paged")
    ResponseEntity<Page<CategoryResponseDTO>> getChildrenCategories(@PathVariable Long parentId,
                                                                    @RequestParam(defaultValue = "0") int page,
                                                                    @RequestParam(defaultValue = "2") int size);

    @GetMapping("/root")
    ResponseEntity<List<CategoryResponseDTO>> getRootCategories();

    /**
     * 특정 카테고리의 직속 하위 카테고리 가져오기
     */
    @GetMapping("/{parentId}/children")
    ResponseEntity<List<CategoryResponseDTO>> getChildrenCategories(@PathVariable Long parentId);
}

