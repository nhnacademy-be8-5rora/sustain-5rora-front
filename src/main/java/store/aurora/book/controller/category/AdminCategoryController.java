package store.aurora.book.controller.category;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import store.aurora.book.dto.category.CategoryRequestDTO;
import store.aurora.book.dto.category.CategoryResponseDTO;
import store.aurora.book.util.PaginationUtil;
import store.aurora.feign_client.book.CategoryClient;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
public class AdminCategoryController {

    private static final String REDIRECT_CATEGORIES = "redirect:/admin/categories";
    private final CategoryClient categoryClient;

    @GetMapping
    public String showRootCategories(@RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "5") int size,
                                     Model model) {
        ResponseEntity<Page<CategoryResponseDTO>> response = categoryClient.getRootCategories(page,size);
        Page<CategoryResponseDTO> categoryPage = Optional.ofNullable(response.getBody()).orElse(Page.empty());

        PaginationUtil.addPaginationAttributes(model, categoryPage, "categories", 5);
        return "admin/category/categories";
    }

    @GetMapping("/{parentId}/children")
    public String showChildCategories(@PathVariable Long parentId,
                                      @RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "5") int size,
                                      Model model) {
        ResponseEntity<Page<CategoryResponseDTO>> response = categoryClient.getChildrenCategories(parentId,page,size);
        Page<CategoryResponseDTO> categoryPage = Optional.ofNullable(response.getBody()).orElse(Page.empty());

        PaginationUtil.addPaginationAttributes(model, categoryPage, "categories", 5);
        model.addAttribute("parentId", parentId);
        return "admin/category/categories";
    }

    @PostMapping("/create")
    public String createCategory(CategoryRequestDTO categoryRequestDTO) {
        categoryClient.createCategory(categoryRequestDTO);
        if (categoryRequestDTO.getParentId() != null) {
            return String.format("redirect:/admin/categories/%d/children", categoryRequestDTO.getParentId());
        }
        return REDIRECT_CATEGORIES;
    }

    @PostMapping("/update")
    public String updateCategory(@RequestParam Long categoryId, @RequestParam String name,
                                 @RequestParam(required = false) Long parentId) {
        CategoryRequestDTO updateRequest = new CategoryRequestDTO();
        updateRequest.setName(name);
        categoryClient.updateCategory(categoryId, updateRequest);
        if (parentId != null) {
            return String.format("redirect:/admin/categories/%d/children", parentId);
        }
        return REDIRECT_CATEGORIES;
    }

    @PostMapping("/delete")
    public String deleteCategory(@RequestParam Long categoryId) {
        categoryClient.deleteCategory(categoryId);
        return REDIRECT_CATEGORIES;
    }


}
