package store.aurora.book;

import jdk.jfr.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import store.aurora.book.dto.category.CategoryDTO;
import store.aurora.feignClient.book.CategoryClient;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryClient categoryClient;

    @Autowired
    public CategoryService(@Lazy CategoryClient categoryClient) {
        this.categoryClient = categoryClient;
    }

    public List<CategoryDTO> findCategoriesByParentId(Long parentId) {
        parentId = parentId == null ? 0 : parentId;
        ResponseEntity<List<CategoryDTO>> response = categoryClient.findCategoriesByParentId(parentId);
        return response.getBody();
    }
}
