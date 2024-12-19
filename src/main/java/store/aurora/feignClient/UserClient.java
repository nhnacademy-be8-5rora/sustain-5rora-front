package store.aurora.feignClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import store.aurora.auth.dto.response.UserPwdAndRoleResponse;
import store.aurora.auth.dto.response.UserUsernameAndRoleResponse;
import store.aurora.config.security.constants.SecurityConstants;
import store.aurora.user.dto.request.SignUpRequest;

import java.util.Map;

@FeignClient(name = "userClient", url = "${api.gateway.base-url}" + "/api/users")
public interface UserClient {

    @GetMapping("/auth/details")
    ResponseEntity<UserPwdAndRoleResponse> getPasswordAndRole(@RequestHeader("UserId") String userId);

    @GetMapping("/auth/me")
    ResponseEntity<UserUsernameAndRoleResponse> getUsernameAndRole(@RequestHeader(SecurityConstants.AUTHORIZATION_HEADER) String jwtToken);

    @GetMapping("/auth/exists")
    Boolean checkUserExists(@RequestHeader("userId") String userId);

    //회원가입
    @PostMapping
    ResponseEntity<Map<String, String>> signUp(@RequestBody SignUpRequest request,
                                               @RequestParam boolean isOauth);

}
