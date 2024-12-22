package store.aurora.book;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import store.aurora.feignClient.book.CategoryClient;
import store.aurora.book.dto.category.CategoryRequestDTO;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class CategoryClientTest {

    @Autowired
    private CategoryClient categoryClient;

    @Test
    public void testCreateCategory() {
        // 카테고리 생성 DTO
        CategoryRequestDTO requestDTO = new CategoryRequestDTO(null, "테스트 카테고리");

        // API 호출
        categoryClient.createCategory(requestDTO);

        // 성공적으로 실행되는지 확인 (ResponseEntity<Void>를 사용하는 경우 Exception 없으면 성공)
        assertThat(true).isTrue();
    }
}