package store.aurora.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import feign.Client;

@Configuration
public class FeignConfig {
    @Bean
    public Client feignClient() {
        return new feign.httpclient.ApacheHttpClient();
    }
}
