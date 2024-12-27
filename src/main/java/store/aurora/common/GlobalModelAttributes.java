package store.aurora.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import store.aurora.book.CategoryService;
import store.aurora.book.dto.category.CategoryDTO;

import java.util.List;

@ControllerAdvice
public class GlobalModelAttributes {

    @Autowired
    private CategoryService categoryService;

    @ModelAttribute("categories")
    public List<CategoryDTO> findCategoriesByParentId(@RequestParam(value = "parentId", defaultValue = "0") Long parentId) {
        return categoryService.findCategoriesByParentId(parentId);
    }

}

