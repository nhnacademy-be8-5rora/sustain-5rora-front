package store.aurora.book.controller;


import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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

import java.util.Collections;
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
    public ResponseEntity<List<CategoryDTO>> getCategory(@PathVariable("categoryId") Long categoryId) {
        if (categoryId == null) {
            categoryId = 0L;
        }
        ResponseEntity<CategoryDTO> categories = categoryClient.findById(categoryId);
        if (categories.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)  // JSON 형식으로 설정
                    .body(Objects.requireNonNull(categories.getBody()).getChildren());
        }
        return ResponseEntity.badRequest().build();
    }

    @GetMapping
    public String showRootCategories(@RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "2") int size,
                                     Model model) {
        ResponseEntity<Page<CategoryResponseDTO>> response = categoryClient.getRootCategories(page,size);
        Page<CategoryResponseDTO> categoryPage = response.getBody();
        if (categoryPage != null) {
            model.addAttribute("categories", categoryPage.getContent());
            model.addAttribute("currentPage", categoryPage.getNumber());
            model.addAttribute("totalPages", categoryPage.getTotalPages());
        } else {
            model.addAttribute("categories", Collections.emptyList());
            model.addAttribute("currentPage", 0);
            model.addAttribute("totalPages", 0);
        }
        return "admin/category/categories";
    }

    @GetMapping("/{parentId}/children")
    public String showChildCategories(@PathVariable Long parentId,
                                      @RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "2") int size,
                                      Model model) {
        ResponseEntity<Page<CategoryResponseDTO>> response = categoryClient.getChildrenCategories(parentId,page,size);
        Page<CategoryResponseDTO> categoryPage = response.getBody();
        if (categoryPage != null) {
            model.addAttribute("categories", categoryPage.getContent());
            model.addAttribute("currentPage", categoryPage.getNumber());
            model.addAttribute("totalPages", categoryPage.getTotalPages());
        } else {
            model.addAttribute("categories", Collections.emptyList());
            model.addAttribute("currentPage", 0);
            model.addAttribute("totalPages", 0);
        }
        return "admin/category/categories";
    }

    @PostMapping("/create")
    public String createCategory(CategoryRequestDTO categoryRequestDTO) {
        categoryClient.createCategory(categoryRequestDTO);
        return "redirect:/categories";
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
