package store.aurora.book;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import store.aurora.book.dto.category.CategoryDTO;
import store.aurora.feign_client.book.CategoryClient;

@Service
public class CategoryService {

    private final CategoryClient categoryClient;


    @Autowired
    public CategoryService(@Lazy CategoryClient categoryClient) {
        this.categoryClient = categoryClient;
    }

    public CategoryDTO findById(Long id) {
        id = id == null ? 0 : id;
        ResponseEntity<CategoryDTO> response = categoryClient.findById(id);
        return response.getBody();
    }
}
