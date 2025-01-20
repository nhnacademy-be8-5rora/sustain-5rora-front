package store.aurora.review.dto.response;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ReviewResponse {
    private Long id;
    private Integer rating;
    private String content;
    private List<String> imageFilePath;
    private LocalDate reviewCreateAt;
    private Long BookId;
    private String userId;
}
