package store.aurora.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import store.aurora.config.security.authProvider.oauth2AuthProvider.CustomAuthorizationRequestResolver;
import store.aurora.config.security.authProvider.oauth2AuthProvider.CustomAccessTokenResponseClient;
import store.aurora.config.security.authProvider.oauth2AuthProvider.CustomOauth2UserService;
import store.aurora.config.security.filter.CookieToHeaderFilter;
import store.aurora.config.security.filter.JwtAuthenticationFilter;
import store.aurora.config.security.handler.loginHandler.success.FormLoginSuccessHandler;
import store.aurora.config.security.handler.loginHandler.success.OauthLoginSuccessHandler;
import store.aurora.config.security.handler.logoutHandler.success.CommonLogoutSuccessHandler;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    //필터
    //private final CookieToHeaderFilter cookieToHeaderFilter;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    //로그인 핸들러
    private final FormLoginSuccessHandler formLoginSuccessHandler;
    private final CommonLogoutSuccessHandler commonLogoutSuccessHandler;
    private final OauthLoginSuccessHandler oauthLoginSuccessHandler;

    //일반 로그인 관련
    private final UserDetailsService apiUserDetailsService;

    //oauth2관련
    private final CustomAuthorizationRequestResolver customAuthorizationRequestResolver;
    private final CustomAccessTokenResponseClient customAccessTokenResponseClient;
    private final CustomOauth2UserService customOauth2UserService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);

        //JSESSIONID로 만드는 세션끄기
        http.sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        //인증, 인가 설정
        http.authorizeHttpRequests(authorizeRequests ->
                authorizeRequests
                        .requestMatchers("/", "/login", "/login/process","/logout", "/oauth2-test", "/login/oauth2/code/**", "/signup", "/cart/**","/books/search","/books/**").permitAll()  //todo /signup 추가
                        .anyRequest().authenticated()
        );

        //daoAuthenticationProvider설정
        http.userDetailsService(apiUserDetailsService);

        //필터 추가
        http.addFilterBefore(jwtAuthenticationFilter, OAuth2LoginAuthenticationFilter.class);
        //http.addFilterBefore(cookieToHeaderFilter, JwtAuthenticationFilter.class);

        //로그인 설정
        http.formLogin(formLogin -> formLogin
                .loginPage("/login")
                .usernameParameter("username")
                .passwordParameter("password")
                .loginProcessingUrl("/login/process")
                .successHandler(formLoginSuccessHandler)
                .failureUrl("/login")
        ).oauth2Login(oauth2 -> oauth2
                .loginPage("/login")
                .authorizationEndpoint(authorization -> authorization.authorizationRequestResolver(customAuthorizationRequestResolver))
                .userInfoEndpoint(userInfo -> userInfo.userService(customOauth2UserService))
                .tokenEndpoint(token -> token.accessTokenResponseClient(customAccessTokenResponseClient))
                .successHandler(oauthLoginSuccessHandler)
        );

        //로그아웃 설정
        http.logout(formLogout -> formLogout
                .logoutUrl("/logout")
                .logoutSuccessHandler(commonLogoutSuccessHandler)
        );

        //예외처리 todo
//        http.exceptionHandling(exception -> exception
//                .authenticationEntryPoint() //인증
//                .accessDeniedHandler()      //인가
//        );

        return http.build();
    }

}
