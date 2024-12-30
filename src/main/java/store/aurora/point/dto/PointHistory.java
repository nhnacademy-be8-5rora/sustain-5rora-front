package store.aurora.point.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class PointHistory {
    private long id;
    private int pointAmount;
    private String pointType;
    private LocalDateTime transactionDate;
    private String from;
}