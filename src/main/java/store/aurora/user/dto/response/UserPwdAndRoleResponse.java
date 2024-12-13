package store.aurora.user.dto.response;

import store.aurora.user.domain.Role;

public record UserPwdAndRoleResponse(String password, Role role) {
}
