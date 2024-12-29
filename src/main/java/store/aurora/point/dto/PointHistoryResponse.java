package store.aurora.point.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PointHistoryResponse {
    private int totalPages;
    private int totalElements;
    private boolean first;
    private boolean last;
    private int size;
    private List<PointHistory> content;
    private int number;
    private int numberOfElements;
    private boolean empty;
}