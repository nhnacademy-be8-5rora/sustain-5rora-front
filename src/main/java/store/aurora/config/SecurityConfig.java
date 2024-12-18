package store.aurora.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import store.aurora.config.security.filter.CookieToHeaderFilter;
import store.aurora.config.security.filter.JwtAuthenticationFilter;
import store.aurora.config.security.handler.loginHandler.success.CommonLoginSuccessHandler;
import store.aurora.config.security.handler.logoutHandler.success.CommonLogoutSuccessHandler;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsService apiUserDetailsService;

    private final CookieToHeaderFilter cookieToHeaderFilter;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    private final CommonLoginSuccessHandler commonLoginSuccessHandler;
    private final CommonLogoutSuccessHandler commonLogoutSuccessHandler;

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
//                        .requestMatchers("/", "/login", "/login/process","/logout", "/cart").permitAll()  //todo /signup 추가
                        .anyRequest().permitAll()
                        .anyRequest().authenticated()
        );

        //daoAuthenticationProvider설정
        http.userDetailsService(apiUserDetailsService);

        //필터 추가
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(cookieToHeaderFilter, JwtAuthenticationFilter.class);

        //로그인 설정
        http.formLogin(formLogin -> formLogin
                .loginPage("/login")
                .usernameParameter("username")
                .passwordParameter("password")
                .loginProcessingUrl("/login/process")
                .successHandler(commonLoginSuccessHandler)
                .failureUrl("/login")
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

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
