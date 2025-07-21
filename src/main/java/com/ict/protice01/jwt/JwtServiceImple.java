package com.ict.protice01.jwt;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class JwtServiceImple implements JwtService{
    private final JwtUtil jwtUtil;
    /** 토큰에서 ID 추출 */
    @Override
    public String GetUserIDFromToken(String token) {
        return jwtUtil.ValidateAndExtractUserID(token);
    }
}
