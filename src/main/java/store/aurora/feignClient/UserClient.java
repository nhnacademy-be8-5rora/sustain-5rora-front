package store.aurora.feignClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import store.aurora.auth.dto.response.UserPwdAndRoleResponse;
import store.aurora.auth.dto.response.UserUsernameAndRoleResponse;
import store.aurora.config.security.constants.SecurityConstants;
import store.aurora.user.dto.request.SignUpRequest;

import java.util.Map;

@FeignClient(name = "userClient", url = "${api.gateway.base-url}" + "/api/users")
public interface UserClient {

    @GetMapping("/auth/details")
    UserPwdAndRoleResponse getPasswordAndRole(@RequestHeader("UserId") String userId);

    @GetMapping("/auth/me")
    UserUsernameAndRoleResponse getUsernameAndRole(@RequestHeader(SecurityConstants.AUTHORIZATION_HEADER) String jwtToken);

    @GetMapping("/auth/exists")
    Boolean checkUserExists(@RequestHeader("userId") String userId);

    //회원가입
    @PostMapping
    Map<String, String> signUp(@RequestBody SignUpRequest signUpRequest,
                               @RequestParam boolean isOauth);

}
