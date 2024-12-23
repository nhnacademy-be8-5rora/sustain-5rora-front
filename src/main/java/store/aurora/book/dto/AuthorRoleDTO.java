package store.aurora.book.dto;



import com.fasterxml.jackson.annotation.JsonProperty;

public class AuthorRoleDTO {

    private Long id;
    private Role role;

    public enum Role {
        @JsonProperty("author")
        AUTHOR,

        @JsonProperty("editor")
        EDITOR
    }
}

