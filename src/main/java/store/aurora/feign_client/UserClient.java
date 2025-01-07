package store.aurora.feign_client;

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
    ResponseEntity<Boolean> checkUserExists(@RequestHeader("userId") String userId);

    //회원가입
    @PostMapping
    ResponseEntity<Map<String, String>> signUp(@RequestBody SignUpRequest request,
                                               @RequestParam boolean isOauth);

    // 회원가입 > 인증번호받기
    @PostMapping("/send-verification-code")
    ResponseEntity<Map<String, String>> sendCode(@RequestBody SignUpRequest request);

    // 회원가입 > 인증코드 검증
    @PostMapping("/verify-code")
    ResponseEntity<Map<String, String>> verifyCode(@RequestBody SignUpRequest request);


    // 회원탈퇴
    @DeleteMapping("/{userId}")
    ResponseEntity<Map<String, String>> deleteUser(@PathVariable String userId);

    // 휴면해제처리
    @PostMapping("/reactivate")
    ResponseEntity<Map<String, String>> reactivateUser(@RequestParam String userId);
}
