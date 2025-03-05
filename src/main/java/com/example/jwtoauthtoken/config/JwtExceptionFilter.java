package com.example.jwtoauthtoken.config;


import com.example.jwtoauthtoken.model.error.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtExceptionFilter extends OncePerRequestFilter { // JWT 검증 과정 중 예외가 발생

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            filterChain.doFilter(request, response);
        } catch (JwtException exception) {
            response.setContentType(MediaType.APPLICATION_JSON_VALUE); //클라이언트에게 반환할 응답의 내용 타입을 JSON 형식으로 설정
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); //상태 코드를 401 Unauthorized로 설정
            response.setCharacterEncoding("UTF-8"); //응답의 문자 인코딩을 UTF-8로 설정
            var errorResponse = new ErrorResponse(HttpStatus.UNAUTHORIZED, exception.getMessage());

            //JSON으로 직렬화한다는 것은 객체를 JSON 형식의 문자열로 변환하는 것을 의미
            ObjectMapper objectMapper = new ObjectMapper(); //ObjectMapper를 생성하여 자바 객체를 JSON 문자열로 변환
            String responseJson = objectMapper.writeValueAsString(errorResponse); //writeValueAsString: 주어진 객체를 JSON 문자열로 변환하는 메서드
            response.getWriter().write(responseJson);//변환된 JSON 문자열을 응답 본문에 쓴다
        }
    }
}
