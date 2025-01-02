package store.aurora.config.security.authProvider.oauth2AuthProvider;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.stereotype.Component;
import store.aurora.auth.dto.Oauth2AuthorizationRequestDto;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class CustomAuthorizationRequestRepository implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {

    private static final String REPOSITORY_COOKIE_KEY = "oauth2";
    private static final Logger log = LoggerFactory.getLogger("user-logger");

    private final RedisTemplate<String, Oauth2AuthorizationRequestDto> template;
    private final ObjectMapper objectMapper;

    @Override
    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {


        Optional<String> optionalCookie = getCookie(request);
        if(optionalCookie.isEmpty()){
            log.info("oauth2 cookie is empty");
            return null;
        }

        OAuth2AuthorizationRequest oAuth2AuthorizationRequest = Oauth2AuthorizationRequestDto.makeOauth2AuthorizationRequest(template.opsForValue().get(optionalCookie.get()));

        if(Objects.isNull(oAuth2AuthorizationRequest)){
            log.info("oAuth2AuthorizationRequest is null. No value found in Redis for key: {}", optionalCookie.get());
            return null;
        }

        //csrf 검사
        if (!isCsrfValid(request, oAuth2AuthorizationRequest)) {
            return null;
        }

        return oAuth2AuthorizationRequest;
    }



    @Override
    public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest, HttpServletRequest request, HttpServletResponse response) {


        if (authorizationRequest == null) {
            removeAuthorizationRequest(request, response);
            return;
        }

        String redisKey = UUID.randomUUID().toString();
        template.opsForValue().set(redisKey, new Oauth2AuthorizationRequestDto(authorizationRequest), 60, TimeUnit.SECONDS);

        Cookie cookie = makeCookie(redisKey, 60);
        response.addCookie(cookie);

    }



    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request, HttpServletResponse response) {
        OAuth2AuthorizationRequest authorizationRequest = loadAuthorizationRequest(request);

        //제거로직
        if(Objects.nonNull(authorizationRequest)){
            Optional<String> optionalCookie = getCookie(request);
            if(optionalCookie.isPresent()){
                //레디스에서 제거
                template.delete(optionalCookie.get());
            }
            //쿠키 만료시키기
            response.addCookie(makeCookie("", 0));
        }

        return authorizationRequest;
    }

    private Optional<String> getCookie(HttpServletRequest request){
        if(Objects.isNull(request) || Objects.isNull(request.getCookies())){
            return Optional.empty();
        }

        for (Cookie cookie : request.getCookies()) {
            if(cookie.getName().equals(REPOSITORY_COOKIE_KEY)){
                return Optional.of(cookie.getValue());
            }
        }

        return Optional.empty();
    }

    private static boolean isCsrfValid(HttpServletRequest request, OAuth2AuthorizationRequest oAuth2AuthorizationRequest) {
        String stateParameter = request.getParameter(OAuth2ParameterNames.STATE);
        if(Objects.isNull(stateParameter)
                || Objects.isNull(oAuth2AuthorizationRequest.getState())
                || !oAuth2AuthorizationRequest.getState().equals(stateParameter)){
            log.info("CSRF token mismatch. Expected: {}, Provided: {}", oAuth2AuthorizationRequest.getState(), stateParameter);
            return false;
        }
        return true;
    }

    private static Cookie makeCookie(String redisKey, int expiration) {
        Cookie cookie = new Cookie(REPOSITORY_COOKIE_KEY, redisKey);
        cookie.setPath("/login/oauth2");
        cookie.setMaxAge(expiration);
        cookie.setHttpOnly(true);
        return cookie;
    }
}
