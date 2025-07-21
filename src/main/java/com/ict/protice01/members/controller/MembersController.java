package com.ict.protice01.members.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ict.protice01.members.dto.MembersMyPageDTO;
import com.ict.protice01.members.entity.MembersEntity;
import com.ict.protice01.members.service.MembersService;
import com.ict.protice01.vo.DataVO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.Map;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MembersController {
    private final MembersService membersService;

    @PostMapping("/login")
    public DataVO Login(@RequestBody MembersEntity membersEntity) {
        try {
            Map<String, String> tokens = membersService.LoginCheck(membersEntity);
            return new DataVO(true, tokens, "로그인 성공");
        } catch (Exception e) {
            return new DataVO(false, null, e.getMessage());
        }
    }

    @PostMapping("/logout")
    public DataVO Logout(HttpServletRequest request) {
        SecurityContextHolder.clearContext();
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return new DataVO(true, null, "Logout OK");
    }

    @GetMapping("/mypage")
    public DataVO GetMyPage(HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization").replace("Bearer ", "");
            MembersMyPageDTO myPageDTO = membersService.GetMyPage(token);
            return new DataVO(true, myPageDTO, "성공");
        } catch (Exception e) {
            return new DataVO(false, null, e.getMessage());
        }
    }

    @PostMapping("/refresh")
    public DataVO GetRefreshToken(@RequestBody Map<String, String> token) {
        try {
            Map<String, String> tokens = membersService.GetRefreshToken(token);
            return new DataVO(true, tokens, "토큰 재발급 완료");
        } catch (Exception e) {
            return new DataVO(false, null, e.getMessage());
        }
    }

}
