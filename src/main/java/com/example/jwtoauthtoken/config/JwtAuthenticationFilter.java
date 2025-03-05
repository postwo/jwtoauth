package com.example.jwtoauthtoken.config;

import com.example.jwtoauthtoken.service.JwtService;
import com.example.jwtoauthtoken.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter { //OncePerRequestFilter 한 요청당 한 번만 실행

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // todo: jwt 인증 로직
        String BEARER_PREFIX = "Bearer ";
        var authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        var securityContext = SecurityContextHolder.getContext();

        if (!ObjectUtils.isEmpty(authorization) && //authorization이 값이 비어있지 않으면서
                authorization.startsWith(BEARER_PREFIX) && //BEARER_PREFIX로 시작하는 경우에만 jwt 인증로직 실행
                securityContext.getAuthentication() == null) { //getAuthentication(=인증정보) 가 null이냐

            var accessToken = authorization.substring(BEARER_PREFIX.length());
            var username = jwtService.getUsername(accessToken);


            var userDetails = userService.loadUserByUsername(username);

            // 인증정보를 담고 있다
            //null= 이부분은 사용자 비밀번호를 뜻한다 = 이미 인증된 사용자이기 때문에 빼도 상관없다,getAuthorities= 권한목록
            var authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

            // 현재 처리 되고있는 api 정보도 담아준다
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // 생성이 완료된 인증정보를securityContext 세팅
            securityContext.setAuthentication(authenticationToken);

            // SecurityContextHolder에다가 securityContext를 세팅 해주면 jwt인증 과정이 끝난다
            SecurityContextHolder.setContext(securityContext);
        }

            //모든 작업이 끝난 후, 필터 체인의 다음 필터로 요청과 응답을 전달합니다. 이를 통해 다른 필터들이 정상적으로 동작할 수 있게 해준다
        filterChain.doFilter(request, response);
    }


}

