package store.aurora.book.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AuthorDTO {
    private String name;
    private String role;

    public AuthorDTO(String name, String role) {
        this.name = name;
        this.role = role;
    }

    // Getters and setters
}

