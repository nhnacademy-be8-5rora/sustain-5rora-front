package store.aurora.feignClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import store.aurora.auth.dto.request.JwtRequestDto;
import store.aurora.config.security.constants.SecurityConstants;
import store.aurora.auth.dto.response.UserPwdAndRoleResponse;
import store.aurora.auth.dto.response.UserUsernameAndRoleResponse;

@FeignClient(name = "authClient", url = "${api.gateway.base-url}" + "/api/auth")
public interface AuthClient {

    @PostMapping
    String getJwtToken(@RequestBody JwtRequestDto jwtRequestDto);

    @GetMapping("/details")
    UserPwdAndRoleResponse getPasswordAndRole(@RequestHeader("UserId") String userId);

    @GetMapping("/me")
    UserUsernameAndRoleResponse getUsernameAndRole(@RequestHeader(SecurityConstants.AUTHORIZATION_HEADER) String jwtToken);
}
