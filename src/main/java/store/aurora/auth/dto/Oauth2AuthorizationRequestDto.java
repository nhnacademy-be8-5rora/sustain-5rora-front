package store.aurora.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

import java.util.Map;
import java.util.Set;

/*
* oauth2AuthorizationRequest를 레디스에 저장하기 위한 dto
* */

@Getter @Setter
public class Oauth2AuthorizationRequestDto {

    @JsonProperty("authorizationUri")
    private String authorizationUri;

    @JsonProperty("authorizationGrantType")
    private String authorizationGrantType;

    @JsonProperty("responseType")
    private String responseType;

    @JsonProperty("clientId")
    private String clientId;

    @JsonProperty("redirectUri")
    private String redirectUri;

    @JsonProperty("scopes")
    private Set<String> scopes;

    @JsonProperty("state")
    private String state;

    @JsonProperty("additionalParameters")
    private Map<String, Object> additionalParameters;

    @JsonProperty("authorizationRequestUri")
    private String authorizationRequestUri;

    @JsonProperty("attributes")
    private Map<String, Object> attributes;

    public Oauth2AuthorizationRequestDto() {
    }

    public Oauth2AuthorizationRequestDto(OAuth2AuthorizationRequest request) {
        this.authorizationUri = request.getAuthorizationUri();
        this.authorizationGrantType = request.getGrantType().getValue();
        this.responseType = request.getResponseType().getValue();
        this.clientId = request.getClientId();
        this.redirectUri = request.getRedirectUri();
        this.scopes = request.getScopes();
        this.state = request.getState();
        this.additionalParameters = request.getAdditionalParameters();
        this.authorizationRequestUri = request.getAuthorizationRequestUri();
        this.attributes = request.getAttributes();
    }

    public static OAuth2AuthorizationRequest makeOauth2AuthorizationRequest(Oauth2AuthorizationRequestDto dto) {
        return OAuth2AuthorizationRequest
                .authorizationCode()
                .authorizationUri(dto.getAuthorizationUri())
                .clientId(dto.getClientId())
                .redirectUri(dto.getRedirectUri())
                .scopes(dto.getScopes())
                .state(dto.getState())
                .additionalParameters(dto.getAdditionalParameters())
                .attributes(dto.getAttributes())
                .build();
    }
}
