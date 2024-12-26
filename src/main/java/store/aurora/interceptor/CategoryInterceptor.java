package store.aurora.interceptor;

import feign.Response;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import store.aurora.book.dto.category.CategoryDTO;
import store.aurora.feignClient.book.CategoryClient;

import java.util.List;

@Component
public class CategoryInterceptor implements HandlerInterceptor {

    private final CategoryClient categoryClient;

    public CategoryInterceptor(CategoryClient categoryClient) {
        this.categoryClient = categoryClient;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response,
                           Object handler, ModelAndView modelAndView) throws Exception {
        if (modelAndView != null) {
            // 요청 파라미터 또는 속성에서 categoryId를 가져옴
            String categoryIdParam = request.getParameter("categoryId");

            // categoryId가 없으면 기본값 "0"을 사용
            Long categoryId = (categoryIdParam != null && !categoryIdParam.isEmpty())
                    ? Long.parseLong(categoryIdParam) : 0L;

            // categoryId를 사용하여 카테고리 조회
            ResponseEntity<List<CategoryDTO>> categoriesResponse = categoryClient.findCategoriesByParentId(categoryId);

            List<CategoryDTO> categories = categoriesResponse.getBody();

            // 뷰에 카테고리 데이터 추가
            modelAndView.addObject("categories", categories);
        }
    }
}
