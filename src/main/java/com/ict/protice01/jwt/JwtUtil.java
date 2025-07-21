package com.ict.protice01.jwt;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import org.springframework.security.core.userdetails.UserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JwtUtil {
    private final Key secretKey;
    private Long accessTokenValidity;
    private Long refreshTokenValidity;
    
    public JwtUtil(String secret, Long accessTokenValidity, Long refreshTokenValidity) {
        this.accessTokenValidity = accessTokenValidity;
        this.refreshTokenValidity = refreshTokenValidity;
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /** AT 생성 */
    public String GenerateAccessToken(String user_id) {
        return Jwts.builder()
                /** 주제 ID */
                .setSubject(user_id)
                /** 생성 날짜 */
                .setIssuedAt(new Date())
                /** 만료 날짜 */
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenValidity))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    /** RT 생성 */
    public String GenerateRefreshToken(String user_id) {
        return Jwts.builder()
                .setSubject(user_id)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenValidity))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    /** RT 유효기간 반환 */
    public Long GetRefreshTokenValidity() {
        return refreshTokenValidity;
    }

    /** JWT 검증 및 사용자 ID 파싱 */
    public String ValidateAndExtractUserID(String token) throws JwtException {
        Claims claims = ExtractAllClaims(token);
        return claims.getSubject();
    }

    /** Token 만료 확인 */
    public boolean IsTokenExpired(String token) {
        try {
            return ExtractExpiration(token).before(new Date());
        } catch (ExpiredJwtException e) {
            // 만료된 경우
            return true;
        } catch (JwtException e) {
            // 토큰 자체가 잘못된 경우 (ex. 시그니처 에러, 변조 등)
            throw new RuntimeException("유효하지 않은 토큰입니다.");
        }
    }

    /** 만료 날짜 추출 */
    public Date ExtractExpiration(String token) throws JwtException {
        return ExtractAllClaims(token).getExpiration();
    }

    /** 받은 토큰을 이용해서 모든 정보 반환 */
    public Claims ExtractAllClaims(String token) throws JwtException {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /** Token 만료 결과 값 반환 */
    public boolean ValidaTeToken(String jwtToken, UserDetails UserDetails) {
        try {
            Claims claims = ExtractAllClaims(jwtToken);
            String username = claims.getSubject();
            Date expiration = claims.getExpiration();
            return username.equals(UserDetails.getUsername()) && !expiration.before(new Date());
        } catch (Exception e) {
            return false;
        }
    }
}
