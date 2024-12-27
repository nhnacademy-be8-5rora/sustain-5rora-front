package store.aurora.book.controller.category;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import store.aurora.feignClient.book.CategoryClient;
import store.aurora.book.dto.category.CategoryRequestDTO;
import store.aurora.book.dto.category.CategoryResponseDTO;

import java.util.List;

@Controller
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryClient categoryClient;

    public CategoryController(CategoryClient categoryClient) {
        this.categoryClient = categoryClient;
    }

    @GetMapping
    public String categoryPage(Model model) {
        // Fetch categories using Feign Client
        List<CategoryResponseDTO> categories = fetchCategories();
        model.addAttribute("categories", categories);
        model.addAttribute("categoryRequestDTO", new CategoryRequestDTO());
        return "admin/category/categories";
    }

    @PostMapping("/create")
    public String createCategory(@ModelAttribute CategoryRequestDTO categoryRequestDTO) {
        categoryClient.createCategory(categoryRequestDTO);
        return "redirect:/books/create";
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

    private List<CategoryResponseDTO> fetchCategories() {
        // Use Feign Client to fetch categories
        ResponseEntity<List<CategoryResponseDTO>> response = categoryClient.getAllCategories();
        return response.getBody();
    }
}