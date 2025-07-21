package com.ict.protice01.jwt;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtRequestFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final MyUserDetailService myUserDetailService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if ("/api/members/refresh".equals(request.getRequestURI())
                || "/api/guestbook/guestbooklist".equals(request.getRequestURI())
                || "/api/guestbook/guestbookdetail".equals(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }
        /** 들어오는 요청마다 헤더에 Authorization 있고 그거에대한 jwt 검증 하기 위해 추출 */
        final String authorizationHeader = request.getHeader("Authorization");
        String jwtToken = null;
        String userid = null;
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwtToken = authorizationHeader.substring(7);
            try {
                // 토큰 만료 확인
                if (jwtUtil.IsTokenExpired(jwtToken)) {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "토큰이 만료 되었습니다.");
                    log.info("1. 만료된 토큰");
                    return;
                }
                userid = jwtUtil.ValidateAndExtractUserID(jwtToken);
            } catch (ExpiredJwtException e) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "토큰이 만료 되었습니다.");
                log.info("2. 만료된 토큰 : {}", e.getMessage());
                return;
            } catch (JwtException e) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token 처리 오류");
                log.info("3. 잘못된 토큰: {}", e.getMessage());
                return;
            } catch (Exception e) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "토큰 처리 중 오류 발생");
                log.info("기타 오류 : {}", e.getMessage());
                return;
            }
        } else {
        }
        try {
            if (userid != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = myUserDetailService.loadUserByUsername(userid);
                if (jwtUtil.ValidaTeToken(jwtToken, userDetails)) {
                    UsernamePasswordAuthenticationToken authtoken = new UsernamePasswordAuthenticationToken(userDetails,
                            null, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authtoken);
                } else {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "유효하지 않은 토큰");
                    return;
                }
            }
        } catch (JwtException e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "토큰 예외 발생: ");
            log.error("토큰 처리 예외", e);
            return;
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "기타 인증 오류");
            log.error("기타 예외", e);
            return;
        }
        filterChain.doFilter(request, response);
    }

}
