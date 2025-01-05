package store.aurora.book.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class BookDetailsUpdateDTO {

    @NotBlank
    private String title;

    private String contents;

    @NotBlank
    private String isbn;

    @NotNull
    private LocalDate publishDate;

    private String publisherName;

    private String seriesName;

    @NotBlank
    private String explanation;

    private boolean isSale;
}