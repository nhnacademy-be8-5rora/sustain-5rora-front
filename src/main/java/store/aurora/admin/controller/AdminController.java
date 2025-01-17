package store.aurora.admin.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import store.aurora.feign_client.search.ElasticSearchClient;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final ElasticSearchClient elasticSearchClient;


    @GetMapping
    public String adminPage(Model model) {
        model.addAttribute("title", "관리자 페이지");
        return "admin/admin";
    }

    @PostMapping("/books/elasticSearch/sync")
    public ResponseEntity<Long> syncBooksToElasticSearch(HttpServletRequest request, Model model) {
        try {
            // 엘라스틱서치에 데이터 저장
            ResponseEntity<Long> countResponse = elasticSearchClient.syncBooksToElasticSearch();

            // count가 null이면 0으로 처리
            Long count = countResponse.getBody();
            if (count == null) {
                count = 0L;
            }

            return ResponseEntity.ok(count);  // count가 0일 경우에도 정상 반환

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

}