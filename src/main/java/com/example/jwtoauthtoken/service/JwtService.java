package com.example.jwtoauthtoken.service;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);
    private final SecretKey key;

    public JwtService(@Value("${jwt.secret-key}") String key) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(key)); //encode 되어있는 시크릿키를 다시 BASE64 로 decode 를 한다
    }

    // 사용자 인증을 통해 인증을 완료된 유저의 username을 subject로 대입해서 토큰을 만들어 accesstoken이라는 형태로 제공함수를 만든다
    public String generateAccessToken(UserDetails userDetails) {
        return generateToken(userDetails.getUsername(),1000 * 60 * 60 * 3);
    }

    //refreshtoken
    public String generateRefreshToken(UserDetails userDetails) {
        return generateToken(userDetails.getUsername(), 1000 * 60 * 60 * 24 * 7); // 1주일 유효
    }

    // 그리고 그렇게 생성된 accessToken으로부터 다시 username을 추출해서 전달해주는 getUsername 함수 생성
    public String getUsername(String accessToken) {
        return getSubject(accessToken);
    }

    // jwt 생성
    private String generateToken(String subject,long expirationMillis) { //토큰에 담을 subject를 담는다
        var now = new Date();
        var exp = new Date(now.getTime() + expirationMillis);
        return Jwts.builder().subject(subject).signWith(key)
                .issuedAt(now) //토큰 발행시점 현재
                .expiration(exp) //만료 시점은 현재로 부터 3시간 뒤
                .compact();
    }

    //jwt 에서 subject를 추출하는 함수
    private String getSubject(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(key) // key값으로 먼저 검증
                    .build()
                    // 해당 토근에 Payload에 저장 되이있더 subject값을 추출
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();
        } catch (JwtException exception) { //검증 실패시 처리
            logger.error("JwtException", exception);
            throw exception;
        }
    }

}
