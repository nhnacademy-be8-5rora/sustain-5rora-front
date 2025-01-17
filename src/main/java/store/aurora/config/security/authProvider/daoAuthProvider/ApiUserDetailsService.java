package store.aurora.config.security.authProvider.daoAuthProvider;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import store.aurora.auth.dto.response.UserPwdAndRoleResponse;
import store.aurora.config.security.exception.DormantAccountException;
import store.aurora.feign_client.UserClient;

@Component
@RequiredArgsConstructor
public class ApiUserDetailsService implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger("user-logger");

    private final UserClient userClient;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        ResponseEntity<UserPwdAndRoleResponse> passwordAndRole;
        try{
            passwordAndRole = userClient.getPasswordAndRole(username);

            //code에 따른 처리
            HttpStatusCode statusCode = passwordAndRole.getStatusCode();
            if(statusCode.isSameCodeAs(HttpStatusCode.valueOf(404))){
                log.info(String.format("해당 회원 없음: username=%s", username));
                throw new UsernameNotFoundException(String.format("해당 회원 없음: username=%s", username));
            } else if (statusCode.is4xxClientError() || statusCode.is5xxServerError()) {
                log.error("status code={}", statusCode);
                throw new AuthenticationServiceException(String.format("status code=%s", statusCode));
            }

        }catch (FeignException e){
            if(e.status() == 403) {
                log.info(String.format("휴면 계정: username=%s", username));
                throw new DormantAccountException(String.format("휴면 계정: username=%s", username));
            }

            log.info("통신 실패 message={}", e.getMessage());
            throw new AuthenticationServiceException(String.format("통신 실패 message=%s", e.getMessage()));

        }

        return new ApiUserDetails(username, passwordAndRole.getBody().password(), passwordAndRole.getBody().role());
    }
}
