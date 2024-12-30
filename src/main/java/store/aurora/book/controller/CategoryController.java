package store.aurora.book.controller;


import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import store.aurora.book.dto.category.CategoryDTO;
import store.aurora.book.dto.category.CategoryRequestDTO;
import store.aurora.book.dto.category.CategoryResponseDTO;
import store.aurora.feignClient.book.CategoryClient;

import java.util.List;

@Controller
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryClient categoryClient;

//    @ModelAttribute("categories")
//    public List<CategoryDTO> getCategories(HttpServletRequest request) {
//        String categoryIdParam = request.getParameter("categoryId");
//        Long categoryId = (categoryIdParam != null && !categoryIdParam.isEmpty())
//                ? Long.parseLong(categoryIdParam) : 0L;
//
//        // 카테고리 조회
//        ResponseEntity<List<CategoryDTO>> categoriesResponse = categoryClient.findCategoriesByParentId(categoryId);
//        return categoriesResponse.getBody();
//    }



    @GetMapping
    public String showCategoryPage(Model model) {
        ResponseEntity<List<CategoryResponseDTO>> response = categoryClient.getCategoryHierarchy();
        model.addAttribute("categories", response.getBody());
        return "admin/category/categories";
    }


    @PostMapping("/create")
    public String createCategory(CategoryRequestDTO categoryRequestDTO) {
        categoryClient.createCategory(categoryRequestDTO);
        return "redirect:/categories";
    }
    @GetMapping("/{categoryId}")
    public ResponseEntity<List<CategoryDTO>> getCategory(@PathVariable("categoryId") Long categoryId) {
        if (categoryId == null) {
            categoryId = 0L;
        }
        ResponseEntity<List<CategoryDTO>> categories = categoryClient.findCategoriesByParentId(categoryId);
        if (categories.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)  // JSON 형식으로 설정
                    .body(categories.getBody());
        }
        return ResponseEntity.badRequest().build();
    }

    @PostMapping("/update")
    public String updateCategory(@RequestParam Long categoryId, @RequestParam String name) {
        CategoryRequestDTO updateRequest = new CategoryRequestDTO();
        updateRequest.setName(name);
        categoryClient.updateCategoryName(categoryId, updateRequest);
        return "redirect:/categories";
    }

    @PostMapping("/delete")
    public String deleteCategory(@RequestParam Long categoryId) {
        categoryClient.deleteCategory(categoryId);
        return "redirect:/categories";
    }

}
