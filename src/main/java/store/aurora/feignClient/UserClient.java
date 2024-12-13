package store.aurora.feignClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import store.aurora.config.security.constants.SecurityConstants;
import store.aurora.user.dto.response.UserPwdAndRoleResponse;
import store.aurora.user.dto.response.UserUsernameAndRoleResponse;

@FeignClient(name = "userClient", url = "${api.shop.base-url}" + "/api/users")
public interface UserClient {

    @GetMapping
    UserPwdAndRoleResponse getPasswordAndRole(@RequestHeader("UserId") String userId);

    @GetMapping         //todo url 정해지지 않음
    UserUsernameAndRoleResponse getUsernameAndRole(@RequestHeader(SecurityConstants.AUTHORIZATION_HEADER) String jwtToken);
}
