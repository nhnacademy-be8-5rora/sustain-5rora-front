package store.aurora.review.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL) // null 값인 필드는 제외
@JsonIgnoreProperties(ignoreUnknown = true) // 알 수 없는 속성은 무시
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ReviewResponse {
    private Long id;
    private Integer rating;
    private String content;
    private List<String> imageFilePath;
    private LocalDateTime reviewCreateAt;
    private Long BookId;
    private String title;
    private String author;
    private String cover;
    private String userId;
}
