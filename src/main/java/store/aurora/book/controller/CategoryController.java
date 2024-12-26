package store.aurora.book.controller;


import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import store.aurora.book.dto.category.CategoryDTO;
import store.aurora.feignClient.book.CategoryClient;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryClient categoryClient;

    @ModelAttribute("categories")
    public List<CategoryDTO> getCategories(HttpServletRequest request) {
        String categoryIdParam = request.getParameter("categoryId");
        Long categoryId = (categoryIdParam != null && !categoryIdParam.isEmpty())
                ? Long.parseLong(categoryIdParam) : 0L;

        // 카테고리 조회
        ResponseEntity<List<CategoryDTO>> categoriesResponse = categoryClient.findCategoriesByParentId(categoryId);
        return categoriesResponse.getBody();
    }
}
