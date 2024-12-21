package store.aurora.feignClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import store.aurora.auth.dto.request.JwtRequestDto;

@FeignClient(name = "authClient", url = "${api.gateway.base-url}" + "/api/auth")
public interface AuthClient {

    @PostMapping
    ResponseEntity<String> getJwtToken(@RequestBody JwtRequestDto jwtRequestDto);
}
