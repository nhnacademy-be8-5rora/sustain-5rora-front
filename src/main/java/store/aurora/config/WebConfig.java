package store.aurora.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.filter.HiddenHttpMethodFilter;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import store.aurora.common.argumentResolver.AuthUsernameResolver;
import store.aurora.config.converter.StringToLongListConverter;
import store.aurora.config.converter.StringToStringListConverter;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final AuthUsernameResolver authUsernameResolver;

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/login").setViewName("login");
        registry.addViewController("/reactive").setViewName("reactive");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(authUsernameResolver);
    }

    @Bean
    public HiddenHttpMethodFilter hiddenHttpMethodFilter() {
        return new HiddenHttpMethodFilter();
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        //사용자가 입력한 값을 콤마(,)로 구분해서 변환

        //text -> List<String>으로 변환 추가
        registry.addConverter(new StringToStringListConverter());
        //text -> List<Long>으로 변환 추가
        registry.addConverter(new StringToLongListConverter());
    }
}
