package store.aurora.book.dto;



public class AuthorRoleDTO {
    private Long id;
    private Role role;

    public enum Role {
        AUTHOR,
        EDITOR
    }
}
