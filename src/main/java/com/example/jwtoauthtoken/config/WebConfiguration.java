package com.example.jwtoauthtoken.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class WebConfiguration {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private JwtExceptionFilter jwtExceptionFilter;

    //CORS 설정: configuration.setAllowedOrigins(List.of("http://localhost:3000", "http://127.0.0.1:3000"));는 http://localhost:3000과 http://127.0.0.1:3000에서 오는 요청을 허용합니다.
    //목적: 이렇게 설정하면, 이 두 페이지에서 http://localhost:8080 백엔드 서버에 접근하여 요청을 보낼 수 있게 됩니다
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        //CORS 설정을 통해 지정된 출처 (예: "http://localhost:3000", "http://127.0.0.1:3000")에서 백엔드 서버 (예: "http://localhost:8080")로의 요청이 허용된다는 의미
        configuration.setAllowedOrigins(List.of("http://localhost:3000", "http://127.0.0.1:3000"));
        //HTTP 메서드를 지정된 출처에서 허용한다는 의미입니다. 다시 말해, 특정 출처(예: "http://localhost:3000", "http://127.0.0.1:3000")에서 어떤 HTTP 메서드를 사용할 수 있는지를 설정한것
        configuration.setAllowedMethods(List.of("GET", "POST", "PATCH", "DELETE"));
        //모든 헤더를 허용 , 여기 헤더를 통해 jwt,엑세스토큰값을 전달 받는다
        configuration.setAllowedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 특정 url 패턴에서만 위 cors 방식을 설정해준다
        source.registerCorsConfiguration("/api/v1/**", configuration);
        return source;
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http    .cors(Customizer.withDefaults()) // cors는 기본값으로 동작

                .authorizeHttpRequests(
                        (requests) ->
                                requests
                                        .requestMatchers(HttpMethod.POST, "/api/*/users", "/api/*/users/authenticate")
                                        .permitAll() // 위 두군데 한해서만 모든 사용자가 사요할 수 있게 허용해준다는의미이다
                                        .anyRequest()
                                        .authenticated()

                )
                .sessionManagement( //RESTful API에서는 보통 세션을 생성하지 않는 것이 일반적 , 세션 생성 x
                        (session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(CsrfConfigurer::disable) //csrf 설정 필요없다 그러므로 꺼준다
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class) //지정된 필터가 다른 특정 필터보다 먼저 실행되도록 한디
                .addFilterBefore(jwtExceptionFilter, jwtAuthenticationFilter.getClass()) //jwtExceptionFilter가 jwtAuthenticationFilter.getClass() 보다 먼저 실행된다
                .httpBasic(HttpBasicConfigurer::disable); // 시큐리티에서 기본적으로 활성화시켜주는 basic도 꺼준다

        return http.build();
    }
}

