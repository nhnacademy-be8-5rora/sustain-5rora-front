package store.aurora.auth.properties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@AllArgsConstructor
@Getter
@ConfigurationProperties("auth.payco")
public class PaycoProperties {
    private String clientId;
    private String clientSecret;
}
