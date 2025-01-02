package store.aurora.book;

import lombok.*;

import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class CustomPage<T> {
    private List<T> content; // 실제 데이터
    private int currentPage; // 현재 페이지 번호
    private int totalPages;  // 총 페이지 수
    private long totalElements; // 총 요소 개수
    private int size;
    }