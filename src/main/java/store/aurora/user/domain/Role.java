package store.aurora.user.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Role {

    ROLE_ADMIN("관리자"),
    ROLE_USER("일반회원");

    private final String value;
}
