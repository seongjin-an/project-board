package com.ansj.demo.config;

import com.ansj.demo.dto.UserAccountDto;
import com.ansj.demo.dto.security.BoardPrincipal;
import com.ansj.demo.dto.security.KakaoOAuth2Response;
import com.ansj.demo.repository.UserAccountRepository;
import com.ansj.demo.service.UserAccountService;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfig {
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        return http
//                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
//                .formLogin().and()
//                .build();
//    }

    /**
     * Configuring HttpSecurity(스프링 시큐리티의 관리하에, 인증과 권한을 체크한다고 보면 됨)
     * 허용된 요청 메서드는 GET 이 유일.
     * 허용된 path는 /articles, /articles/search-hashtag
     */
    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http,
            OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService
    ) throws Exception {
//        return http
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
//                        .mvcMatchers(
//                                HttpMethod.GET,
//                                "/",
//                                "/articles",
//                                "/articles/search-hashtag"
//                        ).permitAll()
//                        .anyRequest().authenticated()
//                )
//                .formLogin().and()
//                .logout()
//                .logoutSuccessUrl("/").and()
//                .build();
        return http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                        .mvcMatchers(
                                HttpMethod.GET,
                                "/",
                                "/articles",
                                "/articles/search-hashtag"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(withDefaults())
                .logout(logout -> logout.logoutSuccessUrl("/"))
                .oauth2Login(oauth -> oauth
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(oAuth2UserService)
                        )
                )
                .build();
    }

    /**
     * Configuring WebSecurity(아예 스프링 시큐리티 검사에서 제외하겠다.)
     */
//    @Bean
//    public WebSecurityCustomizer webSecurityCustomizer() {
//        // static resource, css, js
//        return (web) -> web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
//    }

    /**
     * 실제 인증 데이터를 가져오는 서비스 로직을 구현
     */
//    @Bean
//    public UserDetailsService userDetailsService(UserAccountRepository userAccountRepository) {
//        return username -> userAccountRepository.findById(username)
//                .map(UserAccountDto::from)
//                .map(BoardPrincipal::from)
//                .orElseThrow(() -> new UsernameNotFoundException("유저를 찾을 수 없습니다 - username:" + username));
//    }
    @Bean
    public UserDetailsService userDetailsService(UserAccountService userAccountService) {
        return username -> userAccountService
                .searchUser(username)
                .map(BoardPrincipal::from)
                .orElseThrow(() -> new UsernameNotFoundException("유저를 찾을 수 없습니다 - username:" + username));
    }

    @Bean
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService(
            UserAccountService userAccountService,
            PasswordEncoder passwordEncoder
    ) {
        final DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();

        return userRequest -> {
            // loadUser 는 OAuth user service 인터페이스를 구현하고자할 때 구현했어야 했던 함수명이다.
            OAuth2User oAuth2User = delegate.loadUser(userRequest);

            KakaoOAuth2Response kakaoResponse = KakaoOAuth2Response.from(oAuth2User.getAttributes());
            String registrationId = userRequest.getClientRegistration().getRegistrationId(); // "kakao"
            String providerId = String.valueOf(kakaoResponse.id());
            String username = registrationId + "_"+ providerId;
            String dummyPassword = passwordEncoder.encode("{bcrypt}dummy");
            return userAccountService.searchUser(username)
                    .map(BoardPrincipal::from)
                    .orElseGet(() ->
                            BoardPrincipal.from(
                                    userAccountService.saveUser(
                                            username,
                                            dummyPassword,
                                            kakaoResponse.email(),
                                            kakaoResponse.nickname(),
                                            null
                                    )
                            )
                    );
        };
    }


    /**
     * 패스워드 인코더
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
