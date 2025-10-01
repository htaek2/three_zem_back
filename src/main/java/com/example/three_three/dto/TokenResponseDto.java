package com.example.three_three.dto;

import lombok.Getter;

@Getter
public class TokenResponseDto {
    private String token;

    public TokenResponseDto(String token) {
        this.token = token;
    }
}
