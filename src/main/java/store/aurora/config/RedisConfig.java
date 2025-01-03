package store.aurora.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import store.aurora.auth.dto.Oauth2AuthorizationRequestDto;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Oauth2AuthorizationRequestDto> oAuth2AuthorizationRequestRedisTemplate(RedisConnectionFactory connectionFactory){
        RedisTemplate<String, Oauth2AuthorizationRequestDto> template = new RedisTemplate<>();

        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        Jackson2JsonRedisSerializer<Oauth2AuthorizationRequestDto> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Oauth2AuthorizationRequestDto.class);

        template.setKeySerializer(stringRedisSerializer);
        template.setValueSerializer(jackson2JsonRedisSerializer);

        template.setHashKeySerializer(stringRedisSerializer);
        template.setHashValueSerializer(jackson2JsonRedisSerializer);

        template.setConnectionFactory(connectionFactory);

        return template;
    }

}
