package store.aurora.feign_client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import store.aurora.auth.dto.request.JwtRequestDto;
import store.aurora.config.security.constants.SecurityConstants;

@FeignClient(name = "authClient", url = "${api.gateway.base-url}" + "/api/auth")
public interface AuthClient {

    @PostMapping
    ResponseEntity<String> getJwtToken(@RequestBody JwtRequestDto jwtRequestDto);

    @PostMapping("/refresh")
    String refreshToken(@RequestHeader(SecurityConstants.REFRESH_TOKEN_COOKIE_NAME) String refresh);
}