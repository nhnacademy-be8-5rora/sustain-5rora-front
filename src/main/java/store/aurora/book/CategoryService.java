package store.aurora.book;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import store.aurora.book.dto.category.CategoryResponseDTO;
import store.aurora.exception.category.CategoryNotFoundException;
import store.aurora.feign_client.book.CategoryClient;

@Service
public class CategoryService {

    private final CategoryClient categoryClient;


    @Autowired
    public CategoryService(@Lazy CategoryClient categoryClient) {
        this.categoryClient = categoryClient;
    }

    public CategoryResponseDTO findById(Long id) {
        id = id == null ? 0 : id;
        ResponseEntity<CategoryResponseDTO> response = categoryClient.findById(id);
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new CategoryNotFoundException(id);  // 구체적인 예외 던지기
        }
        return response.getBody();
    }
}
