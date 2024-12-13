package store.aurora.user.dto.response;

import store.aurora.user.domain.Role;

public record UserUsernameAndRoleResponse(String username, Role role) {
}
