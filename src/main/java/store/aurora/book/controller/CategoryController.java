package store.aurora.book.controller;


import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import store.aurora.book.dto.category.CategoryDTO;
import store.aurora.feignClient.book.CategoryClient;

import java.util.List;
import java.util.Objects;

@Controller
@RequiredArgsConstructor
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryClient categoryClient;


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



}
