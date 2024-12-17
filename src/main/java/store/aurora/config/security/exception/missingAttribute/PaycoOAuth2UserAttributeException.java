package store.aurora.config.security.exception.missingAttribute;

import java.util.List;

public class PaycoOAuth2UserAttributeException extends MissingOAuth2UserAttributeException{

    public PaycoOAuth2UserAttributeException(List<String> missingKeys) {
        super(String.format("OAuth2User is missing required attributes: %s", String.join(", ", missingKeys)));
    }

    public PaycoOAuth2UserAttributeException(String missingKey) {
        super(String.format("OAuth2User is missing required attribute: %s", missingKey));
    }
}
