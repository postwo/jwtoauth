package com.example.jwtoauthtoken.model.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;

@JsonInclude(JsonInclude.Include.NON_EMPTY) //객체를 JSON으로 변환할 때, 비어 있는 값(예: 빈 문자열, 빈 컬렉션 등)을 가진 필드는 JSON 출력에서 제외
public record ErrorResponse(HttpStatus status, String message) {
}