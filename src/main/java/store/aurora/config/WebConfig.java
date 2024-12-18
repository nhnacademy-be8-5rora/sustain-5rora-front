package store.aurora.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.HiddenHttpMethodFilter;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        //todo 테스트 페이지 제거
        registry.addViewController("/").setViewName("index");
        registry.addViewController("/login").setViewName("login");

        // Make sure the HiddenHttpMethodFilter is available
        registry.addRedirectViewController("/cart/{bookId}", "/cart/{bookId}");
    }

//    @Bean
//    public HiddenHttpMethodFilter hiddenHttpMethodFilter() {
//        return new HiddenHttpMethodFilter();
//    }

}
