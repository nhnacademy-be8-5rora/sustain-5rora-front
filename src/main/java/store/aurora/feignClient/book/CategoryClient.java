package store.aurora.feignClient.book;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import store.aurora.book.dto.BookDetailsDto;
import store.aurora.book.dto.category.CategoryDTO;

import java.util.List;

@FeignClient(name = "categoryClient", url = "${api.gateway.base-url}" + "/api/categories")
public interface CategoryClient {

    @GetMapping("/{categoryId}")
    ResponseEntity<List<CategoryDTO>> findCategoriesByParentId(@PathVariable(value = "categoryId") Long categoryId);
}

